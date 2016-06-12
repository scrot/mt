import java.util.Date;
import java.util.List;

/**
 * Created by roy on 6/6/16.
 */
public class DcmecTest1 {
    class Dcmec1 { // +3
        private void m1(Dcmec2 a, Dcmec4 b, Dcmec5 c){}
    }

    class Dcmec2 extends Dcmec1 {
        private void m1(Dcmec1 a){}
    }

    class Dcmec3 {}

    class Dcmec4 extends Dcmec2 {
        private void m1(Dcmec1 a){}
    }

    class Dcmec5 extends Dcmec4 {
        private void m1(Dcmec1 a){}
    }
}
