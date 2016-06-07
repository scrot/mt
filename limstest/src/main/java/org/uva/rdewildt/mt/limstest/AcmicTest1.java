import java.util.Date;
import java.util.List;

/**
 * Created by roy on 6/6/16.
 */
public class AcmicTest1 {
    class Acmic1 {
        public void m1(Acmic1 a, Acmic2 b, Acmic3 c){}
        private void m4(int x, float y, String z, List<String> l){}
    }

    class Acmic2 {}

    class Acmic3 extends Acmic5{}

    class Acmic4 extends Acmic1 {
        private void m1(Acmic1 a, Acmic2 b){} // +1
    }

    class Acmic5 extends Acmic4 {
        private void m1(Acmic1 a, Acmic4 b, Acmic3 c){} // +2
    }
}
