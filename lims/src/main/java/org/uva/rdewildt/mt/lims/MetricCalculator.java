package org.uva.rdewildt.mt.lims;

import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MetricCalculator extends EmptyVisitor {
    private final Map<JavaClass, MetricCounter> metrics;

    private final Map<String, JavaClass> classesMap;
    private final Map<String, Set<String>> classCouplesMap;

    private JavaClass currectClass;
    private Set<String> classResponses;
    private Set<String> classCouples;
    private Set<String> classInstanceVariables;
    private Map<String, Set<String>> methodVariablesMap;

    public MetricCalculator(Path binaryRoot) {
        List<JavaClass> classes = collectClasses(binaryRoot);
        this.metrics = initializeMetrics(classes);
        Repository classRepository = buildClassRepository(binaryRoot);

        this.classesMap = new HashMap<>();
        this.classCouplesMap = new HashMap<>();

        for(JavaClass jclass : classes){
            this.classesMap.put(jclass.getClassName(), jclass);
        }

        for(JavaClass jclass : classes){
            visitJavaClass(classRepository.findClass(jclass.getClassName()));
        }

        updateClassesCbo();
    }

    @Override
    public void visitJavaClass(JavaClass jclass) {
        this.currectClass = jclass;
        this.classResponses = new HashSet<>();
        this.classCouples = new HashSet<>();
        this.classInstanceVariables = new HashSet<>();
        this.methodVariablesMap = new HashMap<>();

        updateSuperClassNoc(jclass);
        updateClassDit(jclass);

        addClassCouplings(jclass);

        for(Field field : jclass.getFields()){
            field.accept(this);
        }

        for(Method method : jclass.getMethods()){
            method.accept(this);
        }

        this.metrics.get(jclass).incrementSize1(jclass.getFields().length);
        this.metrics.get(jclass).incrementSize2(jclass.getMethods().length + jclass.getFields().length);

        updateClassRfc(jclass);
        updateClassDac();
        updateClassCouplingMap(jclass, this.classCouples);
        updateClassLcom(jclass);
    }

    @Override
    public void visitMethod(Method method) {
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(this.currectClass.getConstantPool());
        MethodGen methodGen = new MethodGen(method, this.currectClass.getClassName(), constantPoolGen);

        updateClassWmc(methodGen);
        addToResponses(methodGen, constantPoolGen);
        addMethodCouplings(methodGen);
        addMethodInstanceVariables(methodGen);
        updateClassMpc(methodGen);
        updateClassNom(methodGen);
        updateClassSize1(methodGen);
    }

    @Override
    public void visitField(Field field){
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(this.currectClass.getConstantPool());
        FieldGen fieldGen = new FieldGen(field, constantPoolGen);
        this.classInstanceVariables.add(fieldGen.getName());
        addToCouplings(fieldGen.getType());
        //updateClassDac(fieldGen);
    }

    public Map<String, Metric> getMetrics() {
        Map<String, Metric> smetrics = new HashMap<>();
        for(Map.Entry<JavaClass, MetricCounter> entry : this.metrics.entrySet()){
            smetrics.put(entry.getKey().getClassName(), entry.getValue().getMetric());
        }
        return smetrics;
    }

    private void updateClassWmc(MethodGen methodGen){
        Integer cc = calculateMethodCC(methodGen);
        this.metrics.get(this.currectClass).incrementWmc(cc);
    }

    private void updateClassRfc(JavaClass jclass){
        Integer rfc = classResponses.size();
        this.metrics.get(jclass).incrementRfc(rfc);
    }

    private void updateSuperClassNoc(JavaClass jclass){
        try {
            JavaClass superClass = jclass.getSuperClass();
            if(metrics.containsKey(superClass)){
                metrics.get(superClass).incrementNoc(1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateClassDit(JavaClass jclass){
        try {
            Integer dit = jclass.getSuperClasses().length;
            metrics.get(jclass).incrementDit(dit);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateClassesCbo() {
        Map<String, Set<String>> biCouples = new HashMap<>(this.classCouplesMap);
        for(Map.Entry<String, Set<String>> entry : this.classCouplesMap.entrySet()){
            for(String className : entry.getValue()){
                addValueToMapSet(biCouples, className, entry.getKey());
            }
        }

        for(Map.Entry<String, Set<String>> entry : biCouples.entrySet()){
            JavaClass jclass = this.classesMap.get(entry.getKey());
            if(this.metrics.containsKey(jclass)){
                this.metrics.get(jclass).incrementCbo(entry.getValue().size());
            }
        }
    }

    private void updateClassLcom(JavaClass jclass) {
        int lcom = 0;
        List<Set<String>> methodVariables = new ArrayList<>(this.methodVariablesMap.values());

        for(Integer i = 0; i < methodVariables.size(); i++){
            for(Integer j = i + 1; j < methodVariables.size(); j++){
                Set<String> setI = methodVariables.get(i);
                Set<String> setJ = methodVariables.get(j);

                if(complementIsEmpty(setI, setJ)){
                    lcom++;
                }
                else {
                    lcom--;
                }
            }
        }
        if(lcom <= 0){
            this.metrics.get(jclass).incrementLcom(0);
        }
        else {
            this.metrics.get(jclass).incrementLcom(lcom);
        }
    }

    private void updateClassMpc(MethodGen methodGen){
        if(methodGen.getInstructionList() != null) {
            for (Instruction instruction : methodGen.getInstructionList().getInstructions()) {
                if (instruction instanceof InvokeInstruction) {
                    this.metrics.get(currectClass).incrementMpc(1);
                }
            }
        }
    }

    private void updateClassDac() {
        for(String clazz : classCouples){
            JavaClass jclass = this.classesMap.get(clazz);
            if(jclass != null && (jclass.isInterface() || jclass.isAbstract())){
                this.metrics.get(currectClass).incrementDac(1);
            }
        }
    }

    private void updateClassNom(MethodGen methodGen){
        if(methodGen.isPublic()){
            this.metrics.get(this.currectClass).incrementNom(1);
        }
    }

    private void updateClassSize1(MethodGen methodGen){
        if(methodGen.getInstructionList() != null) {
            this.metrics.get(currectClass).incrementSize1(methodGen.getInstructionList().getInstructions().length);
        }
    }


    private void updateClassCouplingMap(JavaClass jclass, Set<String> classCouples) {
        addValueToMapSet(this.classCouplesMap, jclass.getClassName(), classCouples);
    }

    private void addToResponses(MethodGen methodGen, ConstantPoolGen constantPoolGen){
        this.classResponses.add(methodSignature(methodGen));

        if(methodGen.getInstructionList() != null) {
            for (Instruction instruction : methodGen.getInstructionList().getInstructions()) {
                if (instruction instanceof InvokeInstruction) {
                    InvokeInstruction ii = (InvokeInstruction) instruction;
                    String iiAll = ii.getReferenceType(constantPoolGen).getSignature()
                                    + ii.getMethodName(constantPoolGen)
                                    + Arrays.toString(ii.getArgumentTypes(constantPoolGen));
                    this.classResponses.add(iiAll);

                    String iiClass = ii.getReferenceType(constantPoolGen).getSignature();
                    iiClass = iiClass.substring(1, iiClass.length() - 1).replace('/', '.');
                    addValueToMapSet(this.classCouplesMap, this.currectClass.getClassName(), iiClass);
                }
            }
        }
    }

    private void addClassCouplings(JavaClass jclass){
        try {
            this.classCouples.add(jclass.getSuperClass().getClassName());
            this.classCouples.addAll(Arrays.asList(jclass.getInterfaceNames()));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addMethodCouplings(MethodGen methodGen){
        addToCouplings(methodGen.getReturnType());
        addToCouplings(Arrays.asList(methodGen.getArgumentTypes()));

        for(LocalVariableGen local : methodGen.getLocalVariables()){
            addToCouplings(local.getType());
        }

        this.classCouples.addAll(Arrays.asList(methodGen.getExceptions()));

    }

    private void addToCouplings(Type type){
        if(ReferenceName(type) != null && !ReferenceName(type).equals(this.currectClass.getClassName())){
            String typeString = ReferenceName(type);
            if(typeString != null){
                this.classCouples.add(ReferenceName(type));
            }
        }
    }

    private void addToCouplings(List<Type> types){
        for(Type type : types){
            addToCouplings(type);
        }
    }

    private void addMethodInstanceVariables(MethodGen methodGen) {
        Set<String> locals = new HashSet<>();
        ConstantPoolGen constantPoolGen = methodGen.getConstantPool();
        if(methodGen.getInstructionList() != null) {
            List<Instruction> methodInstructions = Arrays.asList(methodGen.getInstructionList().getInstructions());
            for (Instruction instruction : methodInstructions) {
                if (instruction instanceof PUTFIELD) {
                    String fieldname = ((PUTFIELD) instruction).getFieldName(constantPoolGen);
                    if (this.classInstanceVariables.contains(fieldname) && !fieldname.contains("$")) {
                        locals.add(fieldname);
                    }
                }
            }
            if (locals.size() > 0) {
                this.methodVariablesMap.put(methodSignature(methodGen), locals);
            }
        }
    }

    private boolean complementIsEmpty(Set<String> setI, Set<String> setJ) {
        Set<String> i = new HashSet<>(setI);
        Set<String> j = new HashSet<>(setJ);
        i.removeAll(setJ);
        j.removeAll(setI);
        return j.isEmpty() && i.isEmpty();
    }

    private Integer calculateMethodCC(MethodGen methodGen) {
        if(methodGen.getInstructionList() != null){
            List<Instruction> methodInstructions = Arrays.asList(methodGen.getInstructionList().getInstructions());
            return 1 + getBranch(methodInstructions)
                    + methodGen.getExceptions().length;
        }
        else {
            return 1;
        }
    }

    private Integer getBranch(List<Instruction> methodInstructions) {
        Integer branch = 0;
        for(Instruction instruction : methodInstructions){
            if(instruction instanceof JsrInstruction ||
                    instruction instanceof IfInstruction ||
                    instruction instanceof GotoInstruction){
                branch++;
            }
            /*
            else if (instruction instanceof Select){
                branch += ((Select) instruction).getMatchs().length;
            }
            */
        }
        return branch;
    }

    private String ReferenceName(Type type) {
        if (!(type instanceof ReferenceType)) {
            return null;
        }
        else if(type instanceof ArrayType) {
            ArrayType at = (ArrayType)type;
            return ReferenceName(at.getBasicType());
        }
        else {
            return type.toString();
        }
    }

    private String methodSignature(MethodGen methodGen){
        return "L" + this.currectClass.getClassName() + ';' + methodGen.getName() + Arrays.toString(methodGen.getArgumentTypes());
    }

    private String className(JavaClass jclass){
        if(!jclass.getPackageName().equals("")){
            return jclass.getPackageName() + "." + jclass.getClassName();
        }
        else {
            return jclass.getClassName();
        }
    }

    private Map<JavaClass, MetricCounter> initializeMetrics(List<JavaClass> classes) {
        Map<JavaClass, MetricCounter> emptyMetrics = new HashMap<>();
        for(JavaClass jclass : classes){
            emptyMetrics.put(jclass, new MetricCounter(className(jclass)));
        }
        return emptyMetrics;
    }

    private Repository buildClassRepository(Path binaryPath) {
        ClassPath classPath = new ClassPath(binaryPath.toString());
        List<JavaClass> javaClasses = collectClasses(binaryPath);
        SyntheticRepository repo = SyntheticRepository.getInstance(classPath);

        for(JavaClass javaClass : javaClasses){
            repo.storeClass(javaClass);
        }
        return repo;
    }


    private List<JavaClass> collectClasses(Path jarPath) {
        List<JavaClass> classes = new ArrayList<>();

        try {
            Enumeration<JarEntry> jarFiles = new JarFile(jarPath.toFile()).entries();
            while (jarFiles.hasMoreElements()) {
                String filename = jarFiles.nextElement().getName();
                if (filename.endsWith(".class")) {
                    JavaClass javaClass = new ClassParser(jarPath.toString(), filename).parse();
                    classes.add(javaClass);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private <K, V> void addValueToMapSet(Map<K, Set<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<V>() {{ add(value); }});
        }
        else {
            Set<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }

    private <K, V> void addValueToMapSet(Map<K, Set<V>> map, K key, Set<V> value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
        else {
            Set<V> newvalue = map.get(key);
            newvalue.addAll(value);
            map.put(key, newvalue);
        }
    }

}
