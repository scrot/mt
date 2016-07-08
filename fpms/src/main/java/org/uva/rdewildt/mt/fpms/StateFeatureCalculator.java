package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.gcrawler.git.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.FileCrawler;
import org.uva.rdewildt.mt.utils.GitUtils;
import org.uva.rdewildt.mt.utils.model.git.Fault;
import org.uva.rdewildt.mt.utils.BuildUtils;
import org.uva.rdewildt.mt.utils.MapUtils;
import org.uva.rdewildt.mt.utils.model.lang.Java;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.uva.rdewildt.mt.utils.GitUtils.currentBranch;
import static org.uva.rdewildt.mt.utils.MapUtils.mapValuesUniqueFlatmap;

/**
 * Created by roy on 7/7/16.
 */
public class StateFeatureCalculator implements FeatureCalculator {
    private final Map<String, Feature> features;

    public StateFeatureCalculator(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Boolean onlyOuterClasses) throws Exception {
        this.features = calculateOuterClassStateFeaturesGreedy(binaryRoot, gitRoot, ignoreGenerated, ignoreTests, onlyOuterClasses);
    }

    @Override
    public Map<String, Feature> getFeatures() {
        return features;
    }

    private Map<String, Feature> calculateOuterClassStateFeaturesGreedy(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Boolean onlyOuterClasses) throws Exception {
        System.out.print("Resetting system state to HEAD...");
        GitUtils.gitReset(gitRoot, currentBranch(gitRoot).getName());
        GitUtils.gitPull(gitRoot);
        System.out.println("done");
        System.out.print("Recompiling classes...");
        BuildUtils.buildProject(gitRoot);
        System.out.println("done");

        System.out.print("Calculating features...");
        Crawler gcrawler = new FileCrawler(gitRoot, ignoreGenerated, ignoreTests, false, new Java());
        Map<String, Set<String>> faultClassMap = getFaultClasses(gcrawler.getFaults());
        Set<Fault> faults = mapValuesUniqueFlatmap(gcrawler.getFaults());
        Set<String> faultyclasses = mapValuesUniqueFlatmap(faultClassMap);

        Map<String, Feature> headFeatures = new HashMap<>();
        new StatelessFeatureCalculator(binaryRoot, gitRoot, ignoreGenerated, ignoreTests, onlyOuterClasses).getFeatures().forEach((k, v) -> {
            if (!faultyclasses.contains(v.getClassname())) {
                headFeatures.put(k, v);
            }
        });
        System.out.println("done");

        faults.forEach(fault -> {
            String faultId = fault.getCommit().getId().toString();
            try {
                // first restore git state, 'force pull' online branch
                System.out.print("Resetting system state to commit " + faultId + "...");
                GitUtils.gitReset(gitRoot, currentBranch(gitRoot).getName());
                GitUtils.gitPull(gitRoot);

                // reset to commit before fault commit (using ~)
                GitUtils.gitReset(gitRoot, faultId + '~');
                System.out.println("done");

                System.out.print("Recompiling classes...");
                BuildUtils.buildProject(gitRoot);
                System.out.println("done");

                System.out.print("Calculating features...");
                Map<String, Feature> stateFeatures = new StatelessFeatureCalculator(binaryRoot, gitRoot, ignoreGenerated, ignoreTests, onlyOuterClasses).getFeatures();
                System.out.println("done");

                System.out.print("Merging features...");
                faultClassMap.get(faultId).forEach(classname -> {
                    // check for null, not all source files are compiled
                    if(!stateFeatures.isEmpty() || stateFeatures.get(classname) == null){
                        stateFeatures.get(classname).setClassName(classname + '$' + faultId);
                        headFeatures.put(classname + '$' + faultId, stateFeatures.get(classname));
                    }
                });
                System.out.println("done");
            } catch (Exception e) {
                System.out.println("failed");
                e.printStackTrace();
            }
        });

        return headFeatures;
    }

    private Map<String, Set<String>> getFaultClasses(Map<String, Set<Fault>> classFaults) {
        Map<String, Set<String>> faultclasses = new HashMap<>();
        classFaults.forEach((k, v) -> v.forEach(fault -> MapUtils.addValueToMapSet(faultclasses, fault.getCommit().getId().toString(), k)));
        return faultclasses;
    }
}
