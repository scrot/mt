package feature;

import lims.Metric;

/**
 * Created by roy on 5/22/16.
 */
public class Feature extends Metric {
    private final int faults;
    private final int changes;
    private final int authors;
    private final int age;

    public Feature(Feature f){
        super(f);
        this.faults = f.getFaults();
        this.changes = f.getChanges();
        this.authors = f.getAuthors();
        this.age = f.getAge();

    }

    public Feature(Metric m, int faults, int changes, int authors, int age){
        super(m);
        this.faults = faults;
        this.changes = changes;
        this.authors = authors;
        this.age = age;

    }

    public Feature(int wmc, int noc, int rfc, int cbo, int dit,
                   int lcom, int mpc, int dac, int nom, int size1,
                   int size2, int faults, int changes, int authors, int age) {
        super(wmc, noc, rfc, cbo, dit, lcom, mpc, dac, nom, size1, size2);
        this.faults = faults;
        this.changes = changes;
        this.authors = authors;
        this.age = age;
    }

    public int getFaults() {
        return faults;
    }

    public int getChanges() {
        return changes;
    }

    public int getAuthors() {
        return authors;
    }

    public int getAge() {
        return age;
    }
}
