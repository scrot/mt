package org.uva.rdewildt.mt.utils.splitter.parser.java7;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.uva.rdewildt.mt.utils.splitter.model.ClassSource;
import org.uva.rdewildt.mt.utils.splitter.model.Location;
import org.uva.rdewildt.mt.utils.splitter.model.Position;


import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class SourceVisitor extends Java7BaseVisitor<Void> {
    private final Path path;
    private final Map<String, ClassSource> classSources;

    private String packagePrefix;
    private String oldOuterClass;
    private String outerClass;
    private Map<String, Integer> anonclass;

    public SourceVisitor(Path path) {
        this.path = path;
        this.classSources = new HashMap<>();
        this.packagePrefix = "";

        try {
            Java7Lexer lexer = new Java7Lexer(new ANTLRFileStream(path.toString()));
            Java7Parser parser = new Java7Parser(new CommonTokenStream(lexer));
            ParseTree tree = parser.compilationUnit();
            this.visit(tree);
        } catch (IOException e) {
            System.out.println("Could not read path: " + path.toString());
        }
    }

    public SourceVisitor(Path path, String file) {
        this.path = path;
        this.classSources = new HashMap<>();
        this.packagePrefix = "";

        Java7Lexer lexer = new Java7Lexer(new ANTLRInputStream(file));
        Java7Parser parser = new Java7Parser(new CommonTokenStream(lexer));
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
    public Void visitPackageDeclaration(Java7Parser.PackageDeclarationContext ctx) {
        this.packagePrefix = "";
        for(TerminalNode id : ctx.qualifiedName().Identifier()){
            this.packagePrefix += id.getText() + ".";
        }
        return super.visitPackageDeclaration(ctx);
    }

    @Override
    public Void visitTypeDeclaration(Java7Parser.TypeDeclarationContext ctx) {
        this.outerClass = "";
        this.anonclass = new HashMap<>();
        return super.visitTypeDeclaration(ctx);
    }

    @Override
    public Void visitClassDeclaration(Java7Parser.ClassDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitClassDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitInterfaceDeclaration(Java7Parser.InterfaceDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitInterfaceDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitEnumDeclaration(Java7Parser.EnumDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitEnumDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitAnnotationTypeDeclaration(Java7Parser.AnnotationTypeDeclarationContext ctx) {
        appendOuterClass(ctx.Identifier());
        addClassLocation(ctx);
        super.visitAnnotationTypeDeclaration(ctx);
        this.outerClass = this.oldOuterClass;
        return null;
    }

    @Override
    public Void visitEnumConstant(Java7Parser.EnumConstantContext ctx) {
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
    public Void visitCreator(Java7Parser.CreatorContext ctx) {
        if(ctx.createdName() != null && this.anonclass.get(this.outerClass) != null){
            appendOuterClass(Integer.toString(this.anonclass.get(this.outerClass)));
            addClassLocation(ctx);
            super.visitCreator(ctx);
            this.outerClass = this.oldOuterClass;
            IncreaseAnonClass();
        }
        return null;
    }

    @Override
    public Void visitInnerCreator(Java7Parser.InnerCreatorContext ctx) {
        if(ctx.Identifier() != null){
            appendOuterClass(Integer.toString(this.anonclass.get(this.outerClass)));
            addClassLocation(ctx);
            super.visitInnerCreator(ctx);
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
