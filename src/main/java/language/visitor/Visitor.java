package language.visitor;

import language.Java;
import language.Other;

public interface Visitor<T, C> {
    T visit(Java lang, C context);
    T visit(Other lang, C context);
}
