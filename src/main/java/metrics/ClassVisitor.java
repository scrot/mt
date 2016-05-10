package metrics;

import org.apache.bcel.classfile.JavaClass;
import java.util.*;

public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
    private Map<String, JavaClass> parents;
    private Map<String, MetricCounter> metrics;

    public ClassVisitor(List<JavaClass> classes) {
        this.parents = initializeParents(classes);
        this.metrics = initializeMetrics(classes);

        for(JavaClass jclass : classes){
            visitJavaClass(jclass);
        }
    }

    @Override
    public void visitJavaClass(JavaClass javaClass) {
        updateSuperClassNoc(javaClass.getSuperclassName());
        updateClassDit(javaClass.getClassName());
    }

    public Map<String, MetricCounter> getMetrics() { return metrics; }

    private void updateSuperClassNoc(String superClass){
        if(metrics.containsKey(superClass)){
            metrics.get(superClass).incrementNoc();
        }
    }

    private void updateClassDit(String className){
        MetricCounter counter = this.metrics.get(className);
        while(this.parents.get(className) !=  null){
            counter.incrementDit();
            className = this.parents.get(className).getClassName();
        }
    }

    private Map<String, JavaClass> initializeParents(List<JavaClass> classes){
        Map<String, JavaClass> names = new HashMap<>();
        Map<String, JavaClass> parents = new HashMap<>();

        for(JavaClass javaClass : classes){
            names.put(javaClass.getClassName(), javaClass);
        }

        for(JavaClass javaClass : classes){
            parents.put(javaClass.getClassName(), names.get(javaClass.getSuperclassName()));
        }
        return parents;
    }

    private Map<String, MetricCounter> initializeMetrics(List<JavaClass> classes) {
        Map<String, MetricCounter> emptyMetrics = new HashMap<>();
        for(JavaClass jclass : classes){
            emptyMetrics.put(jclass.getClassName(), new MetricCounter());
        }
        return emptyMetrics;
    }

}
