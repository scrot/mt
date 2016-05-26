package org.uva.rdewildt.mt.xloc.lang;

import org.uva.rdewildt.mt.xloc.lang.visitor.Visitor;

/**
 * Created by roy on 5/26/16.
 */
public class Class extends Language {
    @Override
    public <T, C> T accept(Visitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
