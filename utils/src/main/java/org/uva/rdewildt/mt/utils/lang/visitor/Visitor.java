package org.uva.rdewildt.mt.utils.lang.visitor;

import org.uva.rdewildt.mt.utils.lang.Class;
import org.uva.rdewildt.mt.utils.lang.Java;
import org.uva.rdewildt.mt.utils.lang.Other;

public interface Visitor<T, C> {
    T visit(Java lang, C context);

    T visit(Class lang, C context);

    T visit(Other lang, C context);
}
