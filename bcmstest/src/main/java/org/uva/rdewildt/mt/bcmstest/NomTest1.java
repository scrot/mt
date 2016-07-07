/**
 * Created by roy on 5/22/16.
 * <p>
 * NOM Test 1:
 * Number of public local methods
 * <p>
 * Nom1 = 3
 * Nom2 = 1
 * Nom3 = 1
 */
public class NomTest1 {
    interface Nom3 {
        void func1(); // +1
    }

    class Nom1 {
        public Nom1() {
        } // +1

        public void func1() {
        } // +1

        public String func2() {
            return null;
        } // +1

        private void func3() {
        }
    }

    abstract class Nom2 {
        public abstract void func1(); // +1

        protected void func2() {
        }

        private void func3() {
        }
    }
}
