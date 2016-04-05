package distribution.language.structure;

import distribution.language.visitor.Visitor;

public class Java extends Language {
    @Override
    public <T, C> T accept(Visitor<T, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
