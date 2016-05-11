package metrics;

import org.apache.bcel.Constants;
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

public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
    private final Map<JavaClass, MetricCounter> metrics;
    private final Repository classRepository;

    private JavaClass currectClass;
    private Set<String> classResponses;
    private Set<String> classCouples;

    public ClassVisitor(Path binaryRoot) throws IOException, ClassNotFoundException {
        List<JavaClass> classes = collectClasses(binaryRoot);
        this.metrics = initializeMetrics(classes);
        this.classRepository = buildClassRepository(binaryRoot);

        for(JavaClass jclass : classes){
            visitJavaClass(this.classRepository.findClass(jclass.getClassName()));
        }
    }

    @Override
    public void visitJavaClass(JavaClass jclass) {
        this.currectClass = jclass;
        this.classResponses = new HashSet<>();
        this.classCouples = new HashSet<>();

        updateSuperClassNoc(jclass);
        updateClassDit(jclass);

        try {
            this.classCouples.add(jclass.getSuperClass().getClassName());
            this.classCouples.addAll(Arrays.asList(jclass.getInterfaceNames()));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(Field field : jclass.getFields()){
            addToCouplings(field.getType());
        }

        for(Method method : jclass.getMethods()){
            addToCouplings(Arrays.asList(method.getArgumentTypes()));
            this.classCouples.addAll(Arrays.asList(method.getExceptionTable().getExceptionNames()));
            method.accept(this);
        }

        updateClassRfc(jclass);
    }

    @Override
    public void visitMethod(Method method) {
        //TODO: weighted wmc using McCabe
        updateClassWmc();
        addToResponses(method);
        method.getReturnType();
    }

    public Map<JavaClass, MetricCounter> getMetrics() { return metrics; }

    private void updateClassWmc(){
        this.metrics.get(this.currectClass).incrementWmc(1);
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

    private void addToResponses(Method method){
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(this.currectClass.getConstantPool());
        MethodGen methodGen = new MethodGen(method, this.currectClass.getClassName(), constantPoolGen);

        this.classResponses.add("L" + this.currectClass.getClassName() + ';' + method.getName() + Arrays.toString(method.getArgumentTypes()));

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
    private void addToCouplings(Type type){
        this.classCouples.add(className(type));
    }

    private void addToCouplings(List<Type> types){
        List<String> typeNames = new ArrayList<>();
        for(Type type : types){
            typeNames.add(className(type));
        }
        this.classCouples.addAll(typeNames);
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

    private Map<JavaClass, MetricCounter> initializeMetrics(List<JavaClass> classes) {
        Map<JavaClass, MetricCounter> emptyMetrics = new HashMap<>();
        for(JavaClass jclass : classes){
            emptyMetrics.put(jclass, new MetricCounter());
        }
        return emptyMetrics;
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
