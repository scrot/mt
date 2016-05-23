package org.uva.rdewildt.mt.featureset.splitter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.uva.rdewildt.mt.featureset.splitter.model.ClassSource;
import org.uva.rdewildt.mt.featureset.splitter.model.Location;
import org.uva.rdewildt.mt.featureset.splitter.model.Position;
import org.uva.rdewildt.mt.featureset.splitter.parser.Java8BaseVisitor;
import org.uva.rdewildt.mt.featureset.splitter.parser.Java8Lexer;
import org.uva.rdewildt.mt.featureset.splitter.parser.Java8Parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class SourceVisitor extends Java8BaseVisitor<Void> {
    private final Path path;
    private final Map<String, ClassSource> classSources;

    private String packagePrefix;
    private String oldOuterClass;
    private String outerClass;
    private Map<String, Integer> anonclass;

    public SourceVisitor(Path path) throws IOException {
        this.path = path;
        this.classSources = new HashMap<>();
        this.packagePrefix = "";

        Java8Lexer lexer = new Java8Lexer(new ANTLRFileStream(path.toString()));
        Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
        ParseTree tree = parser.compilationUnit();
        this.visit(tree);

        for(Map.Entry<String, ClassSource> entry : classSources.entrySet()) {
            entry.getValue().collectContent(classSources);
        }
    }

    public SourceVisitor(Path path, String file) {
        this.path = path;
        this.classSources = new HashMap<>();
        this.packagePrefix = "";

        Java8Lexer lexer = new Java8Lexer(new ANTLRInputStream(file));
        Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
        ParseTree tree = parser.compilationUnit();
        this.visit(tree);

        //for(Map.Entry<String, ClassSource> entry : classSources.entrySet()) {
        //    entry.getValue().collectContent(classSources);
        //}
    }

    public Map<String, ClassSource> getClassSources() {
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
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitNormalClassDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitNormalInterfaceDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitEnumDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
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
        this.classSources.put(this.outerClass, new ClassSource(this.outerClass, this.path, null, new Location(start, end, this.path)));
    }

    private void appendOuterClass(TerminalNode jclass){
        if(jclass != null){
            this.oldOuterClass = this.outerClass;
            if(this.outerClass.equals("")){
                this.outerClass = this.packagePrefix + jclass.getText();
            }
            else {
                this.outerClass += "$" + jclass.getText();
            }
            this.anonclass.put(this.outerClass, 1);
        }
    }

    private void appendOuterClass(String jclass){
            this.oldOuterClass = this.outerClass;
            if(this.outerClass.equals("")){
                this.outerClass = this.packagePrefix + jclass;
            }
            else {
                this.outerClass += "$" + jclass;
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
