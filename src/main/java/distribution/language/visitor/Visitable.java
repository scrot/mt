package distribution.language.visitor;

public interface Visitable {
    <T, C> T accept(Visitor<T, C> visitor, C context);
}
