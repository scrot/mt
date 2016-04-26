package xloc;

import lang.Java;
import lang.Other;
import lang.visitor.Visitor;
import xloc.pattern.*;

import java.util.regex.Pattern;

public class XLocPatternFactory implements Visitor<XLocPatternBuilder,Void>  {
    @Override
    public XLocPatternBuilder visit(Java lang, Void context) {
        XLocPatternBuilder javaLocPattern = new XLocPatternBuilder();

        javaLocPattern.addBlankPattern(new BlankPattern(Pattern.compile("\\s*$", Pattern.MULTILINE)));
        javaLocPattern.addCommentPattern(new SlCommentPattern(Pattern.compile("^\\s*//.*$", Pattern.MULTILINE)));
        javaLocPattern.addCommentPattern(new MlCommentPattern(
                Pattern.compile("^\\s*/\\*.*$", Pattern.MULTILINE),
                Pattern.compile("^.*\\*/\\s*$", Pattern.MULTILINE)));

        return javaLocPattern;
    }

    @Override
    public XLocPatternBuilder visit(Other lang, Void context) {
        XLocPatternBuilder javaLocPattern = new XLocPatternBuilder();
        javaLocPattern.addUnknownPattern(new UnknownPattern(Pattern.compile(".*$", Pattern.MULTILINE)));
        return javaLocPattern;
    }

}
