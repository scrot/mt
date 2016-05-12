package metrics;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import utils.MapTransformation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MetricCalculator extends org.apache.bcel.classfile.EmptyVisitor {
    private final Map<JavaClass, MetricCounter> metrics;
    private final Repository classRepository;

    private final Map<String, JavaClass> classesMap;
    private final Map<String, Set<String>> classCouplesMap;

    private JavaClass currectClass;
    private Set<String> classResponses;
    private Set<String> classCouples;
    private Set<String> classInstanceVariables;
    private Map<String, Set<String>> methodVariablesMap;

    public MetricCalculator(Path binaryRoot) throws IOException, ClassNotFoundException {
        List<JavaClass> classes = collectClasses(binaryRoot);
        this.metrics = initializeMetrics(classes);
        this.classRepository = buildClassRepository(binaryRoot);

        this.classesMap = new HashMap<>();
        this.classCouplesMap = new HashMap<>();

        for(JavaClass jclass : classes){
            this.classesMap.put(jclass.getClassName(), jclass);
            visitJavaClass(this.classRepository.findClass(jclass.getClassName()));
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

        updateClassRfc(jclass);
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
    }

    @Override
    public void visitField(Field field){
        this.classInstanceVariables.add(field.getName());
        addToCouplings(field.getType());
    }

    public Map<String, Metric> getMetrics() {
        Map<String, Metric> smetrics = new HashMap<>();
        for(Map.Entry<JavaClass, MetricCounter> entry : this.metrics.entrySet()){
            if(!entry.getKey().getPackageName().equals("")){
                smetrics.put(entry.getKey().getPackageName() + "." + entry.getKey().getClassName(), entry.getValue().getMetric());
            }
            else {
                smetrics.put(entry.getKey().getClassName(), entry.getValue().getMetric());
            }
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
        for(Map.Entry<String, Set<String>> entry : this.classCouplesMap.entrySet()){
            for(String className : entry.getValue()){
                MapTransformation.addValueToMapSet(this.classCouplesMap, entry.getKey(), className);
            }
        }

        for(Map.Entry<String, Set<String>> entry : this.classCouplesMap.entrySet()){
            JavaClass jclass = this.classesMap.get(entry.getKey());
            if(this.metrics.containsKey(jclass)){
                this.metrics.get(jclass).incrementCbo(entry.getValue().size());
            }
        }
    }

    private void updateClassLcom(JavaClass jclass) {
        int lcom = 0;
        List<Set<String>> methodInstanceVariables = new ArrayList<>(this.methodVariablesMap.values());
        for(Integer i = 0; i < methodInstanceVariables.size() - 1; i++){
            for(Integer j = i + 1; j < methodInstanceVariables.size(); j++){
                Set<String> setI = methodInstanceVariables.get(i);
                Set<String> setJ = methodInstanceVariables.get(j);

                if(intersection(setI,setJ).size() == 0){
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


    private void updateClassCouplingMap(JavaClass jclass, Set<String> classCouples) {
        this.classCouplesMap.put(jclass.getClassName(), classCouples);
    }

    private void addToResponses(MethodGen methodGen, ConstantPoolGen constantPoolGen){
        this.classResponses.add(methodSignature(methodGen));

        for(Instruction instruction : methodGen.getInstructionList().getInstructions()){
            if(instruction instanceof InvokeInstruction){
                InvokeInstruction ii = (InvokeInstruction) instruction;
                this.classResponses.add(
                        ii.getReferenceType(constantPoolGen).getSignature()
                        + ii.getMethodName(constantPoolGen)
                        + Arrays.toString(ii.getArgumentTypes(constantPoolGen))
                );
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
        if(!className(type).equals(this.currectClass.getClassName())){
            this.classCouples.add(className(type));
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
        List<Instruction> methodInstructions = Arrays.asList(methodGen.getInstructionList().getInstructions());
        for(Instruction instruction : methodInstructions){
            if(instruction instanceof PUTFIELD){
                String fieldname = ((PUTFIELD) instruction).getFieldName(constantPoolGen);
                if(this.classInstanceVariables.contains(fieldname)){
                    locals.add(fieldname);
                }
            }
        }
        if(locals.size() > 0){
            this.methodVariablesMap.put(methodSignature(methodGen), locals);
        }
    }

    private Integer calculateMethodCC(MethodGen methodGen) {
        List<Instruction> methodInstructions = Arrays.asList(methodGen.getInstructionList().getInstructions());
        return 1 + getBranch(methodInstructions)
                + methodGen.getExceptions().length;
    }

    private Integer getBranch(List<Instruction> methodInstructions) {
        Integer branch = 0;
        for(Instruction instruction : methodInstructions){
            if(instruction instanceof JsrInstruction){
                branch++;
            }
            else if (instruction instanceof IfInstruction){
                branch++;
            }
            else if (instruction instanceof Select){
                branch += ((Select) instruction).getMatchs().length;
            }
        }
        return branch;
    }

    private String className(Type type) {
        if (type.getType() <= Constants.T_VOID) {
            return "java.PRIMITIVE";
        }
        else if(type instanceof ArrayType) {
            ArrayType at = (ArrayType)type;
            return className(at.getBasicType());
        }
        else {
            return type.toString();
        }
    }

    private String methodSignature(MethodGen methodGen){
        return "L" + this.currectClass.getClassName() + ';' + methodGen.getName() + Arrays.toString(methodGen.getArgumentTypes());
    }

    private Map<JavaClass, MetricCounter> initializeMetrics(List<JavaClass> classes) {
        Map<JavaClass, MetricCounter> emptyMetrics = new HashMap<>();
        for(JavaClass jclass : classes){
            emptyMetrics.put(jclass, new MetricCounter());
        }
        return emptyMetrics;
    }

    private <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> set = new HashSet<>();

        for (T t : set1) {
            if(set2.contains(t)) {
                set.add(t);
            }
        }
        return set;
    }

    private Repository buildClassRepository(Path binaryPath) throws IOException, ClassNotFoundException {
        ClassPath classPath = new ClassPath(binaryPath.toString());
        List<JavaClass> javaClasses = collectClasses(binaryPath);
        SyntheticRepository repo = SyntheticRepository.getInstance(classPath);

        for(JavaClass javaClass : javaClasses){
            repo.storeClass(javaClass);
        }
        return repo;
    }

    private List<JavaClass> collectClasses(Path jarPath) throws IOException {
        List<JavaClass> classes = new ArrayList<>();

        Enumeration<JarEntry> jarFiles = new JarFile(jarPath.toFile()).entries();
        while(jarFiles.hasMoreElements()){
            String filename = jarFiles.nextElement().getName();
            if(filename.endsWith(".class")){
                JavaClass javaClass = new ClassParser(jarPath.toString(), filename).parse();
                classes.add(javaClass);
            }
        }
        return classes;
    }

}
