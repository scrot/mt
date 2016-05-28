package org.uva.rdewildt.mt.utils.lang;

import org.uva.rdewildt.mt.utils.lang.visitor.Visitor;

public class Other extends Language {
    @Override
    public <T, C> T accept(Visitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
