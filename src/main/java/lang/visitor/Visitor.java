package lang.visitor;

import lang.Java;
import lang.Other;

public interface Visitor<T, C> {
    T visit(Java lang, C context);
    T visit(Other lang, C context);
}
