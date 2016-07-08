package org.uva.rdewildt.mt.utils.model.lang.visitor;

public interface Visitable {
    <T, C> T accept(Visitor<T, C> visitor, C context);
}
