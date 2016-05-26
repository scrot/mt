package org.uva.rdewildt.mt.xloc;

import org.uva.rdewildt.mt.xloc.lang.Java;
import org.uva.rdewildt.mt.xloc.lang.Other;
import org.uva.rdewildt.mt.xloc.lang.Class;
import org.uva.rdewildt.mt.xloc.lang.visitor.Visitor;
import org.uva.rdewildt.mt.xloc.pattern.BlankPattern;
import org.uva.rdewildt.mt.xloc.pattern.MlCommentPattern;
import org.uva.rdewildt.mt.xloc.pattern.SlCommentPattern;
import org.uva.rdewildt.mt.xloc.pattern.XLocPatternBuilder;

import java.util.regex.Pattern;

public class XLocPatternFactory implements Visitor<XLocPatternBuilder,Void>  {
    @Override
    public XLocPatternBuilder visit(Java lang, Void context) {
        return new XLocPatternBuilder()
                .addBlankPattern(new BlankPattern(Pattern.compile("\\s*$", Pattern.MULTILINE)))
                .addCommentPattern(new SlCommentPattern(Pattern.compile("^\\s*//.*$", Pattern.MULTILINE)))
                .addCommentPattern(new MlCommentPattern(
                    Pattern.compile("/\\*", Pattern.MULTILINE),
                    Pattern.compile("\\*/", Pattern.MULTILINE)));
    }

    @Override
    public XLocPatternBuilder visit(Class lang, Void context) {
        return new XLocPatternBuilder();
    }

    @Override
    public XLocPatternBuilder visit(Other lang, Void context) {
        return new XLocPatternBuilder();
    }

}