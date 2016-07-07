package org.uva.rdewildt.mt.fpms;

import org.uva.rdewildt.mt.gcrawler.git.GitUtils;
import org.uva.rdewildt.mt.gcrawler.git.crawler.Crawler;
import org.uva.rdewildt.mt.gcrawler.git.crawler.FileCrawler;
import org.uva.rdewildt.mt.gcrawler.git.model.Fault;
import org.uva.rdewildt.mt.utils.MapUtils;
import org.uva.rdewildt.mt.utils.lang.Java;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.uva.rdewildt.mt.gcrawler.git.GitUtils.currentBranch;
import static org.uva.rdewildt.mt.utils.MapUtils.mapValuesFlatmap;
import static org.uva.rdewildt.mt.utils.MapUtils.mapValuesUniqueFlatmap;

/**
 * Created by roy on 7/7/16.
 */
public class StateFeatureCalculator {
    private final Map<String, Feature> features;

    public StateFeatureCalculator(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Boolean onlyOuterClasses) throws Exception {
        this.features = calculateOuterClassStateFeaturesGreedy(binaryRoot, gitRoot, ignoreGenerated, ignoreTests, onlyOuterClasses);
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    private Map<String, Feature> calculateOuterClassStateFeaturesGreedy(Path binaryRoot, Path gitRoot, Boolean ignoreGenerated, Boolean ignoreTests, Boolean onlyOuterClasses) throws Exception {
        System.out.println("Resetting system state to HEAD...");
        GitUtils.gitReset(gitRoot, currentBranch(gitRoot).getName());
        GitUtils.gitPull(gitRoot);
        System.out.println("Recompiling classes...");
        BuildUtils.buildProject(gitRoot);
        System.out.println("Calculating features...");


        Crawler gcrawler = new FileCrawler(gitRoot, ignoreGenerated, ignoreTests, false, new Java());
        Map<String, Set<String>> faultClassMap = getFaultClasses(gcrawler.getFaults());
        Set<Fault> faults = mapValuesUniqueFlatmap(gcrawler.getFaults());
        Set<String> faultyclasses = mapValuesUniqueFlatmap(faultClassMap);

        Map<String, Feature> headFeatures = new HashMap<>();
        new FeatureCalculator(binaryRoot, gitRoot, ignoreGenerated, ignoreTests, onlyOuterClasses).getFeatures().forEach((k, v) -> {
            if (!faultyclasses.contains(v.getClassname())) {
                headFeatures.put(k, v);
            }
        });

        faults.forEach(fault -> {
            String faultId = fault.getCommit().getId().toString();
            try {
                System.out.println("Resetting system state to commit " + faultId + "...");
                GitUtils.gitReset(gitRoot, "refs/heads/master");
                GitUtils.gitPull(gitRoot);
                GitUtils.gitReset(gitRoot, faultId);

                System.out.println("Recompiling classes...");
                BuildUtils.buildProject(gitRoot);

                System.out.println("Calculating features...");
                Map<String, Feature> stateFeatures = new FeatureCalculator(binaryRoot, gitRoot, ignoreGenerated, ignoreTests, onlyOuterClasses).getFeatures();

                System.out.println("Merging features...");
                faultClassMap.get(faultId).forEach(classname -> {
                    stateFeatures.get(classname).setClassName(classname + '$' + faultId);
                    headFeatures.put(classname + '$' + faultId, stateFeatures.get(classname));
                });
            } catch (Exception ignore) {
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
