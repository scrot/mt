import java.util.Date;
import java.util.List;

/**
 * Created by roy on 6/6/16.
 */
public class OcmicTest1 {
    class Ocmic1 { // +1
        private void m1(Ocmic2 a){} // +1
        public void m2(Ocmic1 a, Ocmic3 b){} // +1
        private void m4(int x, float y, String z, List<String> l){} // +2
    }

    abstract static class Ocmic2 { // static don't count outer class
        public abstract String m1(Ocmic1 a); //+1
        static void m2(Date a){} //+1
        private void m3(){}

    }

    class Ocmic3 {} //+1

    class Ocmic4 extends Ocmic1 { // +1
        private void m1(Ocmic1 a, Ocmic2 b){} // +1
    }

    class Ocmic5 extends Ocmic4 { //+1
        private void m1(Ocmic1 a, Ocmic4 b){}
    }
}
