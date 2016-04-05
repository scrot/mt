package distribution.language.structure;

import distribution.language.visitor.Visitable;

public abstract class Language implements Visitable {
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Language){
            Language l = (Language) o;
            return l.toString().equals(this.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
