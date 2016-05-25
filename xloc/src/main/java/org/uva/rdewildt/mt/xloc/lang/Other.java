package org.uva.rdewildt.mt.xloc.lang;

import org.uva.rdewildt.mt.xloc.lang.visitor.Visitor;

public class Other extends Language {
    @Override
    public <T, C> T accept(Visitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
