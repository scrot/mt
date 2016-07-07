/**
 * Created by roy on 5/21/16.
 * <p>
 * NOC Test 1:
 * Number of immediate children
 * <p>
 * Super = 3
 * Child1 = 2
 * Rest = 0
 */

public class NocTest1 {
    abstract static class Super {
    }

    static class Child1 extends Super {
    }

    static class Child2 extends Super {
    }

    static class Child3 extends Super {
    }

    static class Child11 extends Child1 {
    }

    static class Child12 extends Child1 {
    }

}
