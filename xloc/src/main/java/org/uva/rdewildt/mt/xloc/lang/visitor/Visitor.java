package org.uva.rdewildt.mt.xloc.lang.visitor;

import org.uva.rdewildt.mt.xloc.lang.Class;
import org.uva.rdewildt.mt.xloc.lang.Java;
import org.uva.rdewildt.mt.xloc.lang.Other;

public interface Visitor<T, C> {
    T visit(Java lang, C context);
    T visit(Class lang, C context);
    T visit(Other lang, C context);
}
