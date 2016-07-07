import java.util.List;

/**
 * Created by roy on 5/21/16.
 * <p>
 * CBO Test 1:
 * Coupling between objects
 * <p>
 * Main1 = 9
 * Main2 = 5
 * Couple1 = 4
 * Couple2 = 3
 * Couple3 = 4
 * InnerCouple1 = 2
 */
public class CboTest1 {
    class Main1 {
        // +1 Outer class
        // +1 Coupled to Object
        int x;
        List<InnerCouple1> z; //+1 (Only the list is counted)
        Couple1 c11; // +1

        public Main1() throws Exception { // +1
            System.out.println(new Couple2()); // +2
            Couple1 c12 = new Couple2(); // +0 (Already counted)
        }

        // +1 Main2 coupled to this method
        public Couple3 Return1() {
            return new Couple3(); // +1
        }
    }

    class Main2 {
        // +1 Outer class
        // +1 Coupled to Object
        public Main2() throws Exception { // +1
            Couple3 c3 = new Main1().Return1(); // +2
        }
    }

    abstract class Couple1 {
        // +1 Outer class
        // +1 Coupled to Object
        // +2 Main1 and Couple2 coupled to this method
    }

    class Couple2 extends Couple1 { // +1
        // +1 Outer class
        // +1 Main1 coupled to this method
    }

    class Couple3 {
        // +1 Outer class
        // +1 Coupled to Object
        // +2 Main1 and Main2 coupled to this method
    }

    class InnerCouple1 {
        // +1 Outer class
        // +1 Coupled to Object

    }
}