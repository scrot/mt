import java.util.List;

/**
 * Created by roy on 5/22/16.
 * <p>
 * Dac Test 1:
 * The number of abstract data types defined a class
 * <p>
 * Dac1 = 3
 * Rest = 0
 */
public class DacTest1 {
    enum Enum1 {}

    interface Interface1 {
    }

    class Dac1 {
        Interface1 i1; // +1
        Abstract1 a1; // +1
        Concrete1 c1;
        Enum1 e1;
        List<String> l1;
        List<Abstract1> l2; // +0

        public Dac1() {
            Abstract2 a2 = new Concrete2(); // +1
        }
    }

    abstract class Abstract1 {
    }

    abstract class Abstract2 {
    }

    class Concrete1 extends Abstract1 {
    }

    class Concrete2 extends Abstract2 {
    }
}
