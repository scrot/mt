import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 5/22/16.
 * <p>
 * MPC Test 1:
 * the number of send statements defined in a class
 * <p>
 * Mpc1 = 7
 * Mpc2 = 1
 * Mpc3 = 1
 */
public class MpcTest1 {
    static class Mpc2 {
        // <init> +1
        static void func1() {
        }
    }

    class Mpc1 {
        public Mpc1() {
            // <init> +1
            System.out.println(""); // +1
            List<String> list = new ArrayList<>(); // +1
            Mpc2.func1(); // +1
            new Mpc3(); // +1
            new Mpc3(); // +1
            new Mpc3(); // +1

        }
    }

    private class Mpc3 {
        Mpc3() {
            // <init> +1
        }
    }
}
