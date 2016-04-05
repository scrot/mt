package distribution.language.visitor;

import distribution.language.structure.*;

public interface Visitor<T, C> {
    T visit(Java lang, C context);
    T visit(Other lang, C context);
}
