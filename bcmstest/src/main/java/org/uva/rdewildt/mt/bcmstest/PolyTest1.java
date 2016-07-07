/**
 * Created by roy on 6/7/16.
 */
public class PolyTest1 {
    abstract class Poly1 {
        abstract void polym1(int i);

        abstract void polym1(float f);

        abstract void polym1(int i, float f);

        abstract void polyp1();

        abstract void polyp1(int i);
    }

    abstract class Poly2 extends Poly1 {
        abstract void polym1();
    }

    abstract class Poly3 extends Poly2 {
        abstract void polym1();

        abstract void polyp1();
    }

    abstract class Poly4 {
        abstract void polym1();
    }
}
