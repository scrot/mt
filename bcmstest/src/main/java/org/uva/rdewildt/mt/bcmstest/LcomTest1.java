/**
 * Created by roy on 5/21/16.
 * <p>
 * LCOM Test 1:
 * dissimilarity of methods in a class
 * <p>
 * Lcom1 = 6
 * <p>
 * P = (lcom1, func1),
 * (lcom1, func2),
 * (lcom1, func3),
 * (func1, func2),
 * (func1, func3),
 * (func2, func3)
 * <p>
 * Q = {}
 * <p>
 * Lcom2 = 0 (-6)
 * <p>
 * P = {}
 * Q = (lcom2, func1),
 * (lcom2, func2),
 * (lcom2, func3),
 * (func1, func2),
 * (func1, func3),
 * (func2, func3)
 * <p>
 * Lcom3 = 0
 * <p>
 * P = (lcom3, func1),
 * (lcom3, func2),
 * (func1, func2),
 * <p>
 * Q = (lcom3, func3),
 * (func1, func3),
 * (func2, func3)
 * <p>
 * Lcom4 = 0 (-3)
 * <p>
 * P = {}
 * <p>
 * Q = (lcom4, func1),
 * (lcom4, func2),
 * (func1, func2)
 */
public class LcomTest1 {
    class Lcom1 {

        private int a;
        private int b;

        public Lcom1(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public void func1() {
            a = 0;
            b = 0;
            int none = 0;
        }

        public void func2() {
            a = 0;
            b = 0;
        }

        public void func3() {
            a = 0;
            b = 0;
        }
    }

    class Lcom2 {

        private int a;
        private int b;
        private int c;
        private int d;

        public Lcom2(int a) {
            this.a = a;
        }

        public void func1() {
            b = 0;
        }

        public void func2() {
            c = 0;
        }

        public void func3() {
            d = 0;
        }
    }

    class Lcom3 {

        private int a;
        private int b;
        private int c;

        public Lcom3(int b) {
            this.b = b;
        }

        public void func1() {
            b = 0;
            b = 0;
        }

        public void func2() {
            b = 0;
        }

        public void func3() {
            c = 0;
        }
    }

    class Lcom4 {

        private int a;
        private int b;
        private int c;

        public Lcom4(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public void func1() {
            a = 0;
        }

        public void func2() {
            a = 0;
            b = 0;
            c = 0;
        }
    }
}
