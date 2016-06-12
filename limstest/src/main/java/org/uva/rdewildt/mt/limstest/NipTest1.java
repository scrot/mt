/**
 * Created by roy on 6/7/16.
 */
public class NipTest1 {
    abstract class Nip1 {
        abstract void nipm1(int i);
        abstract void nipm2(float f);
        abstract void nipm3(int i, float f);
        abstract void nipp1();
        abstract void nipp1(int i);
    }

    abstract class Nip2  extends Nip1{
        abstract void nipm1();
    }

    abstract class Nip3 extends Nip2 {
        abstract void nipm1();
        abstract void nipp1();
    }

    abstract class Nip4 { // +3
        abstract void nipm1();
    }
}
