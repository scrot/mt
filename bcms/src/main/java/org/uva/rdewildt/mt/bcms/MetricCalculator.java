package org.uva.rdewildt.mt.bcms;

import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.uva.rdewildt.mt.utils.MapUtils;
import org.uva.rdewildt.mt.utils.lang.Class;
import org.uva.rdewildt.mt.utils.lang.Language;
import org.uva.rdewildt.mt.xloc.PathCollector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MetricCalculator extends EmptyVisitor {
    private final Boolean onlyOuterClasses;

    private final List<JavaClass> classes;
    private final Map<String, Metric> metrics;
    private final Repository classRepository;

    private Map<String, JavaClass> classesMap;
    private Map<String, Set<String>> classCouplesMap;
    private Map<String, Set<Method>> classesMethodMap;
    private Map<String, Set<String>> classesAttributesMap;
    private Map<String,Set<String>> classesMethodArgumentsMap;

    private JavaClass currectClass;
    private Set<String> classResponses;
    private Set<String> classCouples;
    private Set<Method> classMethods;
    private Set<String> classAttributes;
    private Set<String> classMethodArguments;
    private Set<String> classInstanceVariables;

    private Map<String, Set<String>> methodVariablesMap;

    public MetricCalculator(Path binaryRoot, Boolean onlyOuterClasses) {
        this.onlyOuterClasses = onlyOuterClasses;

        this.classes = collectClasses(binaryRoot);
        this.metrics = initializeMetrics(classes);
        this.classRepository = buildClassRepository(binaryRoot, classes);

        start();
    }

    private void start(){
        this.classesMap = new HashMap<>();
        this.classCouplesMap = new HashMap<>();
        this.classesMethodMap = new HashMap<>();
        this.classesAttributesMap = new HashMap<>();
        this.classesMethodArgumentsMap = new HashMap<>();

        for(JavaClass jclass : classes){
            this.classesMap.put(jclass.getClassName(), jclass);
        }

        for(JavaClass jclass : classes){
            visitJavaClass(classRepository.findClass(jclass.getClassName()));
        }

        updateClassesCbo();
        updateClassesMethodCouples();
        updateClassesAttributeCouples();
        updateClassesPoly();
    }

    @Override
    public void visitJavaClass(JavaClass jclass) {
        this.currectClass = jclass;
        this.classResponses = new HashSet<>();
        this.classCouples = new HashSet<>();
        this.classMethods = new HashSet<>();
        this.classAttributes = new HashSet<>();
        this.classMethodArguments = new HashSet<>();
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

        List<Type> classFieldTypes = Arrays.stream(jclass.getFields()).map(Field::getType).collect(Collectors.toList());
        addTypeClasses(this.classAttributes, classFieldTypes);

        getMetric(jclass).incrementSize1(jclass.getFields().length);
        getMetric(jclass).incrementSize2(jclass.getMethods().length + jclass.getFields().length);

        updateClassRfc(jclass);
        updateClassDac();
        updateClassLcom(jclass);

        MapUtils.addValueToMapSet(this.classCouplesMap, getClassName(jclass), this.classCouples);
        MapUtils.addValueToMapSet(this.classesMethodMap, getClassName(jclass), this.classMethods);
        MapUtils.addValueToMapSet(this.classesAttributesMap, getClassName(jclass), this.classAttributes);
        MapUtils.addValueToMapSet(this.classesMethodArgumentsMap, getClassName(jclass), this.classMethodArguments);
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
        addTypeClasses(this.classMethodArguments, Arrays.asList(methodGen.getArgumentTypes()));
        this.classMethods.add(method);
    }

    @Override
    public void visitField(Field field){
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(this.currectClass.getConstantPool());
        FieldGen fieldGen = new FieldGen(field, constantPoolGen);
        this.classInstanceVariables.add(fieldGen.getName());
        addTypeClass(this.classCouples, fieldGen.getType());
        //updateClassDac(fieldGen);
    }

    public Map<String, Metric> getMetrics() {
        return this.metrics;
    }

    private void updateClassWmc(MethodGen methodGen){
        Integer cc = calculateMethodCC(methodGen);
        getMetric(this.currectClass).incrementWmc(cc);
    }

    private void updateClassRfc(JavaClass jclass){
        Integer rfc = classResponses.size();
        getMetric(jclass).incrementRfc(rfc);
    }

    private void updateSuperClassNoc(JavaClass jclass){
        try {
            JavaClass superClass = jclass.getSuperClass();
            if(metricsContains(superClass)){
                getMetric(superClass).incrementNoc(1);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("couldn't find superclass of " + jclass.getClassName());
        }
    }

    private void updateClassDit(JavaClass jclass){
        try {
            Integer current = getMetric(jclass).getDit();
            Integer dit = jclass.getSuperClasses().length > current ? jclass.getSuperClasses().length : current;
            getMetric(jclass).setDit(dit);
        } catch (ClassNotFoundException e) {
            System.out.println("couldn't find superclass of " + jclass.getClassName());
        }
    }

    private void updateClassesCbo() {
        Map<String, Set<String>> biCouples = new HashMap<>(this.classCouplesMap);
        this.classCouplesMap.forEach((k,v) -> v.forEach(name -> MapUtils.addValueToMapSet(biCouples, name, k)));

        biCouples.forEach((k,v) -> {
            JavaClass jclass = this.classesMap.get(k);
            if(metricsContains(jclass)){
                getMetric(jclass).incrementCbo(v.size());
            }
        });
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
        if(lcom > 0) {
            getMetric(jclass).setLcom(lcom);
        }
    }

    private void updateClassMpc(MethodGen methodGen){
        try {
            Arrays.stream(methodGen.getInstructionList().getInstructions()).forEach(instruction -> {
                if (instruction instanceof InvokeInstruction) {
                    getMetric(this.currectClass).incrementMpc(1);
                }
            });
        } catch (Exception ignore){}
    }

    private void updateClassDac() {
        classCouples.forEach(clazz -> {
            JavaClass jclass = this.classesMap.get(clazz);
            if(jclass != null && (jclass.isInterface() || jclass.isAbstract())){
                getMetric(this.currectClass).incrementDac(1);
            }
        });
    }

    private void updateClassNom(MethodGen methodGen){
        if(methodGen.isPublic()){
            getMetric(this.currectClass).incrementNom(1);
        }
    }

    private void updateClassSize1(MethodGen methodGen){
        try {
            getMetric(currectClass).incrementSize1(methodGen.getInstructionList().getInstructions().length);
        } catch (Exception ignore){}
    }

    private void updateClassesMethodCouples(){
        this.classesMethodArgumentsMap.forEach((k, v) -> v.forEach(t -> {
            JavaClass current = this.classesMap.get(k);
            JavaClass type= this.classesMap.get(t);

            if(isAncestor(type, current)){
                if(metricsContains(current)) {
                    getMetric(current).incrementAcmic(1);
                }
                if(metricsContains(type)){
                    getMetric(type).incrementAcmec(1);
                }
            }
            else if (isDecendant(type, current)){
                if(metricsContains(current)) {
                    getMetric(current).incrementDcmic(1);
                }
                if(metricsContains(type)){
                    getMetric(type).incrementDcmec(1);
                }
            }
            else {
                if(metricsContains(current)) {
                    getMetric(current).incrementOcmic(1);
                }
                if(metricsContains(type)){
                    getMetric(type).incrementOcmec(1);
                }
            }
        }));
    }

    private void updateClassesAttributeCouples(){
        this.classesAttributesMap.forEach((k, v) -> v.forEach(t -> {
            JavaClass current = this.classesMap.get(k);
            JavaClass type= this.classesMap.get(t);

            if(isAncestor(type, current)){
                if(metricsContains(current)) {
                    getMetric(current).incrementAcaic(1);
                }
                if(metricsContains(type)){
                    getMetric(type).incrementAcaec(1);
                }
            }
            else if (isDecendant(type, current)){
                if(metricsContains(current)) {
                    getMetric(current).incrementDcaic(1);
                }
                if(metricsContains(type)){
                    getMetric(type).incrementDcaec(1);
                }
            }
            else {
                if(metricsContains(current)) {
                    getMetric(current).incrementOcaic(1);
                }
                if(metricsContains(type)){
                    getMetric(type).incrementOcaec(1);
                }
            }
        }));
    }

    private void updateClassesPoly(){
        Map<String, Set<JavaClass>> methodToClassesMap = new HashMap<>();
        this.classesMethodMap.forEach((k,v) -> {
            Set<String> methodClassNames = new HashSet<>();
            v.forEach(m -> {
                if(!m.getName().contains("<init>")){
                    JavaClass jclass = this.classesMap.get(k);
                    MapUtils.addValueToMapSet(methodToClassesMap,m.getName(), jclass);

                    if(!methodClassNames.contains(m.getName())){
                        methodClassNames.add(m.getName());
                    }
                    else {
                        getMetric(jclass).incrementOvo(1);
                    }
                }
            });
        });

        Set<Pair> spaPairs = new HashSet<>();
        Set<Pair> spdPairs = new HashSet<>();
        Set<Pair> dpaPairs = new HashSet<>();
        Set<Pair> dpdPairs = new HashSet<>();
        Set<Pair> nipPairs = new HashSet<>();

        methodToClassesMap.forEach( (k,v) -> v.forEach(c1 -> v.forEach(c2 -> {
            if(!c1.equals(c2)) {
                List<Method> ms1 = this.classesMethodMap.get(getClassName(c1)).stream().filter(m1 -> k.equals(m1.getName())).collect(Collectors.toList());
                List<Method> ms2 = this.classesMethodMap.get(getClassName(c2)).stream().filter(m2 -> k.equals(m2.getName())).collect(Collectors.toList());
                Pair cpair = new Pair(c1, c2);

                ms1.forEach(m1 -> ms2.forEach(m2 -> {
                    if (!dpaPairs.contains(cpair) && m1.toString().equals(m2.toString()) && isAncestor(c1, c2)) {
                        getMetric(c1).incrementDpa(1);
                        getMetric(c1).incrementDp(1);
                        dpaPairs.add(cpair);
                    } else if (!dpdPairs.contains(cpair) && m1.toString().equals(m2.toString()) && isDecendant(c1, c2)) {
                        getMetric(c1).incrementDpd(1);
                        getMetric(c1).incrementDp(1);
                        dpdPairs.add(cpair);
                    } else if (!spaPairs.contains(cpair) && m1.getName().equals(m2.getName()) && isAncestor(c1, c2)) {
                        getMetric(c1).incrementSpa(1);
                        getMetric(c1).incrementSp(1);
                        spaPairs.add(cpair);
                    } else if (!spdPairs.contains(cpair) && m1.getName().equals(m2.getName()) && isDecendant(c1, c2)) {
                        getMetric(c1).incrementSpd(1);
                        getMetric(c1).incrementSp(1);
                        spdPairs.add(cpair);
                    } else if (!dpaPairs.contains(cpair) && !dpdPairs.contains(cpair) && !spaPairs.contains(cpair) &&
                            !spdPairs.contains(cpair) && !nipPairs.contains(cpair) && m1.getName().equals(m2.getName())){
                        getMetric(c1).incrementNip(1);
                        nipPairs.add(cpair);
                    }
                }));
            }
        })));
    }

    private void addToResponses(MethodGen methodGen, ConstantPoolGen constantPoolGen){
        this.classResponses.add(methodSignature(methodGen));

        try {
            for (Instruction instruction : methodGen.getInstructionList().getInstructions()) {
                if (instruction instanceof InvokeInstruction) {
                    InvokeInstruction ii = (InvokeInstruction) instruction;
                    String iiAll = ii.getReferenceType(constantPoolGen).getSignature()
                            + ii.getMethodName(constantPoolGen)
                            + Arrays.toString(ii.getArgumentTypes(constantPoolGen));
                    this.classResponses.add(iiAll);

                    String iiClass = ii.getReferenceType(constantPoolGen).getSignature();
                    iiClass = iiClass.substring(1, iiClass.length() - 1).replace('/', '.');
                    MapUtils.addValueToMapSet(this.classCouplesMap, getClassName(this.currectClass), getClassName(iiClass));
                }
            }
        } catch (Exception ignore){}
    }

    private void addClassCouplings(JavaClass jclass){
        try {
            this.classCouples.add(jclass.getSuperClass().getClassName());
        }
        catch (ClassNotFoundException e) {
            System.out.println("\t\tcouldn't find superclass of " + jclass.getClassName());
        }
        this.classCouples.addAll(Arrays.asList(jclass.getInterfaceNames()));
    }

    private void addMethodCouplings(MethodGen methodGen){
        addTypeClass(this.classCouples, methodGen.getReturnType());
        addTypeClasses(this.classCouples, Arrays.asList(methodGen.getArgumentTypes()));

        for(LocalVariableGen local : methodGen.getLocalVariables()){
            addTypeClass(this.classCouples, local.getType());
        }

        this.classCouples.addAll(Arrays.asList(methodGen.getExceptions()));

    }

    private void addTypeClass(Collection<String> collection, Type type){
        if(ReferenceName(type) != null && !ReferenceName(type).equals(this.currectClass.getClassName())){
            String typeString = ReferenceName(type);
            if(typeString != null){
                collection.add(ReferenceName(type));
            }
        }
    }

    private void addTypeClasses(Collection<String> collection, List<Type> types){
        for(Type type : types){
            addTypeClass(collection, type);
        }
    }

    private void addMethodInstanceVariables(MethodGen methodGen) {
        Set<String> locals = new HashSet<>();
        ConstantPoolGen constantPoolGen = methodGen.getConstantPool();
        try {
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
        } catch (Exception ignore){}
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
            try {
                List<Instruction> methodInstructions = Arrays.asList(methodGen.getInstructionList().getInstructions());
                return 1 + getBranch(methodInstructions)
                        + methodGen.getExceptions().length;
            } catch (Exception ignore){}
        }
        return 1;
    }

    private Integer getBranch(List<Instruction> methodInstructions) {
        Integer branch = 0;
        for(Instruction instruction : methodInstructions){
            if(instruction instanceof JsrInstruction ||
                    instruction instanceof IfInstruction ||
                    instruction instanceof GotoInstruction){
                branch++;
            }
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

    private Map<String, Metric> initializeMetrics(List<JavaClass> classes) {
        Map<String, Metric> emptyMetrics = new HashMap<>();
        for(JavaClass jclass : classes){
            emptyMetrics.put(getClassName(jclass), new Metric(getClassName(jclass)));
        }
        return emptyMetrics;
    }

    private Repository buildClassRepository(Path binaryPath, List<JavaClass> classes) {
        ClassPath classPath = new ClassPath(binaryPath.toString());
        SyntheticRepository repo = SyntheticRepository.getInstance(classPath);
        classes.forEach(repo::storeClass);
        return repo;
    }


    private List<JavaClass> collectClasses(Path binaryPath) {
        if(binaryPath.toFile().isDirectory()){
            return collectFromDir(binaryPath);
        }
        else {
            return collectFromJar(binaryPath);
        }
    }

    private List<JavaClass> collectFromDir(Path dirPath) {
        List<JavaClass> classes = new ArrayList<>();

        List<Language> scope = new ArrayList<Language>(){{ add(new Class()); }};
        List<Path> paths = new PathCollector(dirPath, true, false, false, scope).getFilePaths().get(new Class());

        paths.forEach(path -> {
            try {
                Path classPath = dirPath.resolve(path);
                classes.add(new ClassParser(classPath.toString()).parse());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return classes;
    }

    private List<JavaClass> collectFromJar(Path jarPath){
        List<JavaClass> classes = new ArrayList<>();
        try (InputStream input = Files.newInputStream(jarPath); ZipInputStream zip = new ZipInputStream(input)){
            ZipEntry zipEntry = zip.getNextEntry();
            while(zipEntry != null){
                String filename = zipEntry.getName();
                if (filename.endsWith(".class")) {
                    JavaClass javaClass = new ClassParser(jarPath.toString(), filename).parse();
                    classes.add(javaClass);
                }
                zipEntry = zip.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private Boolean sameSimpleName(Method m1, Method m2){
        if(m1 == null | m2 == null){
            return false;
        }
        return m1.getName().equals(m2.getName());
    }

    private Boolean isDecendant(JavaClass maybeDecendant, JavaClass jclass){
        return isAncestor(jclass, maybeDecendant);
    }

    private Boolean isAncestor(JavaClass maybeAncestor, JavaClass jclass) {
        if (jclass == null || maybeAncestor == null) {
            return false;
        }

        try {
            List<JavaClass> supers = Arrays.asList(jclass.getSuperClasses());
            if (supers.contains(maybeAncestor)) {
                return true;
            }
        } catch (ClassNotFoundException ignore) {
            return false;
        }
        return false;
    }

    private Metric getMetric(JavaClass jclass){
        return this.metrics.get(getClassName(jclass));
    }

    private String getClassName(String classname){
        if(onlyOuterClasses){
            return getOuterClass(classname);
        }
        else{
            return classname;
        }
    }

    private String getClassName(JavaClass jclass){
        if(jclass == null){
            return "";
        }
        else if(onlyOuterClasses){
            return getOuterClass(jclass);
        }
        else{
            return jclass.getClassName();
        }
    }

    private Boolean metricsContains(JavaClass jclass){
        if(jclass == null){
            return false;
        }
        if(onlyOuterClasses){
            return this.metrics.containsKey(getOuterClass(jclass));
        }
        else{
            return this.metrics.containsKey(jclass.getClassName());
        }
    }

    private String getOuterClass(String classname){
        if(classname.contains("$")){
            return classname.substring(0,classname.indexOf("$"));
        }
        else {
            return classname;
        }
    }

    private String getOuterClass(JavaClass jclass){
        if(jclass == null){
            return "";
        }
        return getOuterClass(jclass.getClassName());
    }

    private class Pair {
        Object o1;
        Object o2;

        Pair(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Pair){
                Pair cp = (Pair) o;
                return o1.equals(cp.o1) && o2.equals(cp.o2) ;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return o1.hashCode() + o2.hashCode() * 31;
        }
    }
}
