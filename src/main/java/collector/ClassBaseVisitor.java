package collector;

import collector.parser.Java8BaseVisitor;
import collector.parser.Java8Lexer;
import collector.parser.Java8Parser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class ClassBaseVisitor extends Java8BaseVisitor<Void> {
    private String packageName;

    public ClassBaseVisitor(Path file) throws IOException {
        this.packageName = "";

        Java8Lexer lexer = new Java8Lexer(new ANTLRFileStream(file.toString()));
        Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
        ParseTree tree = parser.compilationUnit();
        this.visit(tree);
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public Void visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        for(TerminalNode id : ctx.Identifier()){
            this.packageName += id.getText() + ".";
        }
        return super.visitPackageDeclaration(ctx);
    }

    @Override
    public Void visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        this.packageName += ctx.Identifier().getText();
        return null;
    }

    @Override
    public Void visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        this.packageName += ctx.Identifier().getText();
        return null;
    }
}
