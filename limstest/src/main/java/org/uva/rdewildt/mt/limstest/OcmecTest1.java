import java.util.Date;
import java.util.List;

/**
 * Created by roy on 6/6/16.
 */
public class OcmecTest1 {
    class Ocmec1 {}// +2

    class Ocmec2 extends Ocmec5{
        private void m1 (Ocmec1 a, Ocmec2 b, Ocmec3 c){

        }
    }

    abstract class Ocmec3 { //+1
        abstract void m1(Ocmec1 a);
        private void m2(Ocmec1 a){}
        private void m3(Date a){}
        private void m4(List<Ocmec2> a){}

    }

    class Ocmec4 extends Ocmec2 {
        private void m1(Ocmec2 a){}
    }

    class Ocmec5 {
        private void m1(Ocmec2 a){}
    }
}
