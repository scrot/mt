package distribution.xloc;

import distribution.language.structure.Java;
import distribution.language.structure.Other;
import distribution.language.visitor.Visitor;
import distribution.xloc.pattern.BlankPattern;
import distribution.xloc.pattern.MlCommentPattern;
import distribution.xloc.pattern.SlCommentPattern;
import distribution.xloc.pattern.XLocPatternBuilder;

import java.util.regex.Pattern;

public class XLocPatternFactory implements Visitor<XLocPatternBuilder,Void>  {
    @Override
    public XLocPatternBuilder visit(Java lang, Void context) {
        XLocPatternBuilder javaLocPattern = new XLocPatternBuilder();

        javaLocPattern.addBlankPattern(new BlankPattern(Pattern.compile("\\s*")));

        javaLocPattern.addCommentPattern(new SlCommentPattern(Pattern.compile("\\s*//.*")));
        javaLocPattern.addCommentPattern(new MlCommentPattern(
                Pattern.compile("\\s*/\\*"),
                Pattern.compile(".*\\*/\\s*")));

        return javaLocPattern;
    }

    @Override
    public XLocPatternBuilder visit(Other lang, Void context) {
        return null;
    }

}
