package collector;

import collector.model.ClassSource;
import collector.model.Location;
import collector.model.Position;
import collector.parser.Java8BaseVisitor;
import collector.parser.Java8Lexer;
import collector.parser.Java8Parser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassBaseVisitor extends Java8BaseVisitor<Void> {
    private final Path file;
    private final List<ClassSource> classSources;

    private String packagePrefix;
    private String oldOuterClass;
    private String outerClass;
    private Map<String, Integer> anonclass;

    public ClassBaseVisitor(Path file) throws IOException {
        this.file = file;
        this.classSources = new ArrayList<>();
        this.packagePrefix = "";

        Java8Lexer lexer = new Java8Lexer(new ANTLRFileStream(file.toString()));
        Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
        ParseTree tree = parser.compilationUnit();
        this.visit(tree);

        for(ClassSource clazz : classSources){
            clazz.collectContent(classSources);
        }
    }

    public List<ClassSource> getClassSources() {
        return classSources;
    }

    @Override
    public Void visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        this.packagePrefix = "";
        for(TerminalNode id : ctx.Identifier()){
            this.packagePrefix += id.getText() + ".";
        }
        return super.visitPackageDeclaration(ctx);
    }

    @Override
    public Void visitTypeDeclaration(Java8Parser.TypeDeclarationContext ctx) {
        this.outerClass = "";
        this.anonclass = new HashMap<>();
        return super.visitTypeDeclaration(ctx);
    }


    @Override
    public Void visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier().getText());
        addClassLocation(ctx);
        super.visitNormalClassDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier().getText());
        addClassLocation(ctx);
        super.visitNormalInterfaceDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier().getText());
        addClassLocation(ctx);
        super.visitEnumDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier().getText());
        addClassLocation(ctx);
        super.visitAnnotationTypeDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitEnumConstant(Java8Parser.EnumConstantContext ctx) {
        if(ctx.classBody() != null){
            appendOuterClass(Integer.toString(this.anonclass.get(this.outerClass)));
            addClassLocation(ctx);
            super.visitEnumConstant(ctx);
            this.outerClass = this.oldOuterClass;
            IncreaseAnonClass();
        }
        return null;
    }

    @Override
    public Void visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        if(ctx.classBody() != null){
            appendOuterClass(Integer.toString(this.anonclass.get(this.outerClass)));
            addClassLocation(ctx);
            super.visitClassInstanceCreationExpression(ctx);
            this.outerClass = this.oldOuterClass;
            IncreaseAnonClass();
        }
        return null;
    }

    @Override
    public Void visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
        if(ctx.classBody() != null){
            appendOuterClass(Integer.toString(this.anonclass.get(this.outerClass)));
            addClassLocation(ctx);
            super.visitClassInstanceCreationExpression_lf_primary(ctx);
            this.outerClass = this.oldOuterClass;
            IncreaseAnonClass();
        }
        return null;
    }

    @Override
    public Void visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        if (ctx.classBody() != null) {
            appendOuterClass(Integer.toString(this.anonclass.get(this.outerClass)));
            addClassLocation(ctx);
            super.visitClassInstanceCreationExpression_lfno_primary(ctx);
            this.outerClass = this.oldOuterClass;
            IncreaseAnonClass();
        }
        return null;
    }

    private void addClassLocation(ParserRuleContext ctx){
        Position start = new Position(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        Position end = new Position(ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        this.classSources.add(new ClassSource(this.outerClass, this.file, null, new Location(start, end, this.file)));
    }

    private void appendOuterClass(String className){
        this.oldOuterClass = this.outerClass;
        if(this.outerClass.equals("")){
            if(file.toFile().isFile()){
                this.outerClass = this.packagePrefix + className;
            }
        }
        else {
            this.outerClass += "$" + className;
        }
        this.anonclass.put(this.outerClass, 1);
    }

    private void IncreaseAnonClass(){
        if(this.anonclass.containsKey(this.outerClass)){
            Integer i = this.anonclass.get(this.outerClass);
            this.anonclass.put(this.outerClass, i+1);
        }
        else{
            this.anonclass.put(this.outerClass, 1);
        }
    }
}
