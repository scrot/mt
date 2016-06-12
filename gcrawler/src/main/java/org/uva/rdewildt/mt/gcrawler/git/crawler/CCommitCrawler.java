package org.uva.rdewildt.mt.gcrawler.git.crawler;

import org.eclipse.jgit.api.Git;
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
import org.uva.rdewildt.mt.gcrawler.git.model.Commit;
import org.uva.rdewildt.mt.utils.splitter.model.ClassSource;
import org.uva.rdewildt.mt.utils.splitter.parser.java7.SourceVisitor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roy on 5/2/16.
 */
public class CCommitCrawler extends CommitCrawler{
    private final Map<String, Set<Commit>> commits;


    public CCommitCrawler(Git git, List<Path> includes) {
        super(git);
        this.commits = collectChanges(includes);
    }

    @Override
    public Map<String, Set<Commit>> getChanges() {
        return this.commits;
    }

    private Map<String, Set<Commit>> collectChanges(List<Path> includes){
        Map<String, Set<Commit>> classCommits = new HashMap<>();

        System.out.println("Collecting and filtering commit paths...");
        Map<RevCommit, List<Path>> commitPaths =  getFilteredCommitsPaths(includes);

        int i = 0;
        for(Map.Entry<RevCommit, List<Path>> entry : commitPaths.entrySet()){
            System.out.println("Analysing commit " + i++ + " of " + commitPaths.size());
            entry.getValue().stream().forEach(commitPath -> {
                System.out.println("\t Path: " + commitPath.toString());
                String commitSource = getCommitSource(entry.getKey(), commitPath);
                Map<String, ClassSource> commitClasses = new SourceVisitor(commitPath, commitSource).getClassSources();
                Set<String> affectedClasses = classesAffectedByCommit(entry.getKey(), commitClasses);
                affectedClasses.forEach(jclass -> addValueToMapSet(classCommits, jclass, revCommitToCommit(entry.getKey())));
            });
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
            diffs.forEach(diff -> {
                Path path = diff.getChangeType() == DiffEntry.ChangeType.DELETE ? Paths.get(diff.getOldPath()) : Paths.get(diff.getNewPath());

                List<Edit> editList = new ArrayList<>();
                try { editList = diffFormatter.toFileHeader(diff).toEditList(); } catch (IOException e) { e.printStackTrace(); }

                Map<Integer, String> classMap = getClassMap(commitClasses, path);
                editList.forEach(edit -> {
                    int start = edit.getBeginA();
                    int end = edit.getEndA();
                    for(int i = start; i <= end; i++){
                        if(classMap.containsKey(i)){
                            classes.add(classMap.get(i));
                        }
                    }
                });
            });
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
        try {
            TreeWalk treeWalk = new TreeWalk(this.git.getRepository());
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(filePath.toString().replace('\\', '/')));
            treeWalk.next();
            ObjectLoader loader = this.git.getRepository().open(treeWalk.getObjectId(0));
            return new String(loader.getCachedBytes(Integer.MAX_VALUE));
        }
        catch (Exception e){
            e.getStackTrace();
        }

        return "";
    }

}
