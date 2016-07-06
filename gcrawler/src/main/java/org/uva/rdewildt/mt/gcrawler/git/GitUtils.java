package org.uva.rdewildt.mt.gcrawler.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GitUtils {
    public static Map<String, String> gitLastCommits(Map<String, Path> gitRoots){
        Map<String, String> lastcommits = new HashMap<>();
        gitRoots.forEach((k,v) -> {
            try(Git git = gitFromPath(v)){
                if (git != null) {
                    try(Repository repo = git.getRepository()){
                        ObjectId head = repo.resolve(Constants.HEAD);

                        try (RevWalk walk = new RevWalk(repo)) {
                            RevCommit commit = walk.parseCommit(head);
                            lastcommits.put(v.toString(), commit.getName());
                            walk.dispose();
                        }
                    } catch (IOException e) {e.printStackTrace();}
                }
            }});

        return lastcommits;
    }

    public static void gitClone(URIish gitUri, Path path){
        File projectroot= Paths.get(path.toString(), gitUri.getPath().replace('/','-').substring(1)).toFile();
        File gitroot = Paths.get(projectroot.toString(), ".git").toFile();

        if(gitUri.isRemote() && !projectroot.exists()){
            try {
                Git.cloneRepository()
                        .setURI(gitUri.toString())
                        .setDirectory(projectroot)
                        .call();
                System.out.println("cloning into " + gitUri.getHumanishName() + " successful");
            } catch (Exception e) {
                System.out.println("cloning into " + gitUri.getHumanishName() + " failed");
            }
        }
        else if (gitroot.exists()){
            try {
                Git git = Git.open(gitroot);

                StoredConfig config = git.getRepository().getConfig();
                config.setBoolean( "http", null, "sslVerify", false );
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

    public static void gitReset(Path gitPath, String commitId){
        try(Git git = gitFromPath(gitPath)){
            if (git != null) {
                git.reset().setRef(commitId).setMode(ResetCommand.ResetType.HARD).call();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static Git gitFromPath(Path gitPath) {
        File gitFolder = Paths.get(gitPath.toString(), ".git").toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try(Repository repo = builder.setGitDir(gitFolder)
                .readEnvironment()
                .build()){
            return new Git(repo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
