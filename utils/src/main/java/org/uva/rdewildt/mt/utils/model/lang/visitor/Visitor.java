package org.uva.rdewildt.mt.utils.model.lang.visitor;

import org.uva.rdewildt.mt.utils.model.lang.Class;
import org.uva.rdewildt.mt.utils.model.lang.Java;
import org.uva.rdewildt.mt.utils.model.lang.Other;

public interface Visitor<T, C> {
    T visit(Java lang, C context);

    T visit(Class lang, C context);

    T visit(Other lang, C context);
}
