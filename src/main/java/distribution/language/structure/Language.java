package distribution.language.structure;

import distribution.language.visitor.Visitable;

public abstract class Language implements Visitable {
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
