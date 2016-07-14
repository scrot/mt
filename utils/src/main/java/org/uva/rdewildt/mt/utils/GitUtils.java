package org.uva.rdewildt.mt.utils;

import com.jcraft.jsch.IdentityRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.Connector;
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository;
import com.jcraft.jsch.agentproxy.USocketFactory;
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector;
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.FS;
import org.uva.rdewildt.mt.utils.model.git.Author;
import org.uva.rdewildt.mt.utils.model.git.Commit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GitUtils {
    public static List<RevCommit> getCurrentBranchCommits(Path gitRoot) throws IOException, GitAPIException {
        List<RevCommit> commits = new ArrayList<>();
        try (Repository repository = repoFromPath(gitRoot)) {
            try (Git git = new Git(repository)) {
                Iterable<RevCommit> revcommits = git.log().add(currentBranch(gitRoot)).call();
                for (RevCommit revcommit : revcommits) {
                    commits.add(revcommit);
                }
            }
        }
        return commits;
    }

    public static ObjectId currentBranch(Path gitRoot) throws IOException {
        try (Repository repository = repoFromPath(gitRoot)) {
            String remote = repository.getRemoteNames().stream().findFirst().orElse("origin");
            String branch = repository.getBranch()!= null ? repository.getBranch() : "master";
            return repository.resolve("remotes/" + remote + '/' + branch);
        }
    }


    public static List<Path> getCommitPaths(Path gitRoot, RevCommit revCommit) {
        List<Path> files = new ArrayList<>();

        if (revCommit.getParentCount() <= 0) {
            return files;
        }

        try (Repository repo = repoFromPath(gitRoot)) {
            try (ObjectReader reader = repo.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, revCommit.getTree());
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, revCommit.getParent(0).getTree());

                try (Git git = new Git(repo)) {
                    List<DiffEntry> diffs = git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();

                    for (DiffEntry diff : diffs) {
                        if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
                            Path path = Paths.get(diff.getOldPath());
                            files.add(path);
                        } else {
                            Path path = Paths.get(diff.getNewPath());
                            files.add(path);
                        }
                    }
                }
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return files;
    }


    public static Map<RevCommit, List<Path>> getFilteredCommitsPaths(Path gitRoot, List<Path> includes) throws IOException, GitAPIException {
        List<RevCommit> revCommits = getCurrentBranchCommits(gitRoot);
        Map<RevCommit, List<Path>> commitPaths = new HashMap<>();
        revCommits.forEach(revCommit -> {
            List<Path> paths = getCommitPaths(gitRoot, revCommit).stream()
                    .filter(includes::contains)
                    .collect(Collectors.toList());
            if (!paths.isEmpty()) {
                commitPaths.put(revCommit, paths);
            }
        });
        return commitPaths;
    }

    public static Commit revCommitToCommit(RevCommit revCommit) {
        return new Commit(
                revCommit.getName(),
                new Author(revCommit.getAuthorIdent().getName()),
                revCommit.getFullMessage(),
                revCommit.getAuthorIdent().getWhen());
    }

    public static Repository repoFromPath(Path gitPath) throws IOException {
        File gitFolder = Paths.get(gitPath.toString(), ".git").toFile();

        JschConfigSessionFactory sessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {}

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                Connector con = null;
                try {
                    if(SSHAgentConnector.isConnectorAvailable()){
                        USocketFactory usf = new JNAUSocketFactory();
                        con = new SSHAgentConnector(usf);
                    }
                } catch(AgentProxyException e){
                    e.printStackTrace();
                }

                final JSch jsch = super.createDefaultJSch(fs);
                if (con != null) {
                    JSch.setConfig("PreferredAuthentications", "publickey");
                    IdentityRepository identityRepository = new RemoteIdentityRepository(con);
                    jsch.setIdentityRepository(identityRepository);
                }
                return jsch;
            }
        };
        SshSessionFactory.setInstance(sessionFactory);

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(gitFolder)
                .readEnvironment()
                .build();
    }

    public static void gitClone(URIish gitUri, Path path) {
        File projectroot = Paths.get(path.toString(), gitUri.getPath().replace('/', '-').substring(1)).toFile();
        File gitroot = Paths.get(projectroot.toString(), ".git").toFile();

        if (gitUri.isRemote() && !projectroot.exists()) {
            try {
                Git.cloneRepository()
                        .setURI(gitUri.toString())
                        .setDirectory(projectroot)
                        .call();
                System.out.println("cloning into " + gitUri.getHumanishName() + " successful");
            } catch (Exception e) {
                System.out.println("cloning into " + gitUri.getHumanishName() + " failed");
            }
        } else if (gitroot.exists()) {
            try {
                Git git = Git.open(gitroot);

                StoredConfig config = git.getRepository().getConfig();
                config.setBoolean("http", null, "sslVerify", false);
                config.save();

                git.reset().setMode(ResetCommand.ResetType.HARD).call();
                git.pull().call();
                git.close();

                System.out.println("pulling " + gitUri.getHumanishName() + " successful");
            } catch (Exception e) {
                System.out.println("pulling " + gitUri.getHumanishName() + " failed");
            }
        }
    }

    public static void gitReset(Path gitPath, String ref) throws IOException, GitAPIException {
        try (Repository repo = repoFromPath(gitPath)) {
            try (Git git = new Git(repo)) {
                git.reset().setRef(ref).setMode(ResetCommand.ResetType.HARD).call();
            }
        }
    }

    public static void gitFetch(Path gitPath) throws IOException, GitAPIException {
        try (Repository repo = repoFromPath(gitPath)) {
            try (Git git = new Git(repo)) {
                git.fetch().call();
            }
        }
    }

    public static void cRProcedure(Path gitRoot, String faultId) throws IOException, GitAPIException {
        System.out.print("Cleaning local working folder" + "...");
        cleanGitWorkingDir(gitRoot);
        System.out.println("done");
        System.out.print("Resetting system state to commit " + faultId + "...");
        GitUtils.gitFetch(gitRoot);
        GitUtils.gitReset(gitRoot, faultId);
        System.out.println("done");
    }



    public static void cleanGitWorkingDir(Path gitRoot){
        File gitFile = gitRoot.toFile();
        List<File> files = new ArrayList<>();
        if(gitFile.exists() && gitFile.isDirectory()){
            File[] fileArray = gitFile.listFiles();
            if(fileArray != null) {
                files = Arrays.asList(fileArray);
            }
        }
        files.forEach(file -> {
            try {
                if (file.isFile()) {
                    FileUtils.forceDelete(file);
                } else {
                    if(!file.toString().endsWith(".git")) {
                        FileUtils.deleteDirectory(file);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
