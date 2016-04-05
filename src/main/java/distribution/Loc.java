package distribution;

public class Loc {
    private final Integer cloc;

    public Loc(Integer cloc) {
        this.cloc = cloc;
    }

    public Integer getCloc() {
        return cloc;
    }

    @Override
    public String toString() {
        return this.cloc.toString();
    }
}
