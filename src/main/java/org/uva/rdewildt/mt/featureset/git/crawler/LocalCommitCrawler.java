package org.uva.rdewildt.mt.featureset.git.crawler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.uva.rdewildt.mt.featureset.git.model.Author;
import org.uva.rdewildt.mt.featureset.git.model.Commit;
import org.uva.rdewildt.mt.featureset.splitter.SourceVisitor;
import org.uva.rdewildt.mt.featureset.splitter.model.ClassSource;
import org.uva.rdewildt.mt.utils.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roy on 5/2/16.
 */
public class LocalCommitCrawler implements CommitCrawler {
    private final Git git;
    private final Map<String, Set<Commit>> commits;
    private Commit first;
    private Commit last;

    public LocalCommitCrawler(Git git) {
        this.git = git;
        this.commits = collectClassCommits();

    }

    @Override
    public Map<String, Set<Commit>> getCommits() {
        return this.commits;
    }

    private Map<String, Set<Commit>> collectClassCommits(){
        Map<String, Set<Commit>> classCommits = new HashMap<>();

        for(Map.Entry<RevCommit, List<Path>> entry : getCommitsPaths().entrySet()){
            for(Path commitPath : entry.getValue()){
                if(commitPath.toString().endsWith(".java")){
                    String commitSource = getCommitSource(entry.getKey(), commitPath);
                    Map<String, ClassSource> commitClasses = new SourceVisitor(commitPath, commitSource).getClassSources();
                    Set<String> affectedClasses = classesAffectedByCommit(entry.getKey(), commitClasses);
                    for(String jclass : affectedClasses){
                        Utils.addValueToMapSet(classCommits, jclass, RevCommitToCommit(entry.getKey()));
                    }
                }
            }
        }
        return classCommits;
    }

    private Set<String> classesAffectedByCommit(RevCommit commit, Map<String, ClassSource> commitClasses) {
        Set<String> classes = new HashSet<>();

        try {
            ObjectReader reader = this.git.getRepository().newObjectReader();
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, commit.getTree());
            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, commit.getParent(0).getTree());


            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(git.getRepository());
            diffFormatter.setContext(0);

            List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).call();
            for (DiffEntry diff : diffs) {
                Path path = diff.getChangeType() == DiffEntry.ChangeType.DELETE ? Paths.get(diff.getOldPath()) : Paths.get(diff.getNewPath());
                List<Edit> editList = diffFormatter.toFileHeader(diff).toEditList();

                Map<Integer, String> classMap = getClassMap(commitClasses, path);
                for(Edit edit : editList){
                    int start = edit.getBeginA();
                    int end = edit.getEndA();
                    for(int i = start; i <= end; i++){
                        if(classMap.containsKey(i)){
                            classes.add(classMap.get(i));
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.getStackTrace();
        }

        return classes;
    }

    private Map<Integer, String> getClassMap(Map<String, ClassSource> classSources, Path classPath){
        Map<Integer, String> classMap = new HashMap<>();

        List<ClassSource> sortedOnLoc = classSources.values().stream().sorted((c1, c2) -> c1.getLocation().compareTo(c2.getLocation())).collect(Collectors.toList());
        for(ClassSource classSource : sortedOnLoc){
            if (classSource.getSourceFile().equals(classPath)) {
                int start = classSource.getLocation().getStart().getLine();
                int end = classSource.getLocation().getEnd().getLine();
                for (int i = start; i <= end; i++) {
                    classMap.put(i, classSource.getClassName());
                }
            }
        }


        return classMap;
    }

    private String getCommitSource(RevCommit commit, Path filePath) {
        String source = "";

        try {
            TreeWalk treeWalk = new TreeWalk(this.git.getRepository());
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(filePath.toString().replace('\\', '/')));
            treeWalk.next();
            ObjectLoader loader = this.git.getRepository().open(treeWalk.getObjectId(0));
            source = new String(loader.getBytes());
        }
        catch (Exception e){
            e.getStackTrace();
        }

        return source;
    }

    private Map<RevCommit, List<Path>> getCommitsPaths() {
        Map<RevCommit, List<Path>> commitPaths = new HashMap<>();
        try {
            for (RevCommit commit : git.log().call()) {
                commitPaths.put(commit, getCommitFiles(commit));
            }
        }
        catch (Exception e){
            e.getStackTrace();
        }
        return commitPaths;
    }

    private List<Path> getCommitFiles(RevCommit commit) {
        List<Path> files = new ArrayList<>();

        try {
            ObjectReader reader = this.git.getRepository().newObjectReader();
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, commit.getTree());
            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, commit.getParent(0).getTree());

            List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).call();
            for(DiffEntry diff : diffs){
                if (diff.getChangeType() == DiffEntry.ChangeType.DELETE){
                    Path path = Paths.get(diff.getOldPath());
                    files.add(path);
                }
                else {
                    Path path = Paths.get(diff.getNewPath());
                    files.add(path);
                }
            }
        }
        catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        return files;
    }

    private Commit RevCommitToCommit(RevCommit revCommit){
        return new Commit(
                revCommit.getId(),
                new Author(revCommit.getAuthorIdent().getName()),
                revCommit.getFullMessage(),
                revCommit.getAuthorIdent().getWhen());
    }
}
