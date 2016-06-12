import java.util.Date;
import java.util.List;

/**
 * Created by roy on 6/6/16.
 */
public class DcmicTest1 {
    class Dcmic1 { // +2
        private void m1(Dcmic2 a, Dcmic5 b){}
        public void m2(Dcmic1 a, Dcmic3 b){}
        private void m4(int x, float y, String z, List<String> l){}
    }

    class Dcmic2 extends Dcmic1 {
        void m2(Date a){}
        private void m3(){}

    }

    class Dcmic3 {}

    class Dcmic4 extends Dcmic2 {
        private void m1(Dcmic1 a, Dcmic2 b){}
    }

    class Dcmic5 extends Dcmic4 {
        private void m1(Dcmic1 a, Dcmic4 b){}
    }
}
