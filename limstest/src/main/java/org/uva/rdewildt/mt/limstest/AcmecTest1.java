import java.util.Date;
import java.util.List;

/**
 * Created by roy on 6/6/16.
 */
public class AcmecTest1 {
    class Acmec1 {
        private void m1(Acmec2 a){}
    }

    class Acmec2 extends Acmec1 { //+1
        private void m1(Acmec1 a, Acmec2 b, Acmec3 c){}
    }

    class Acmec3 extends Acmec2{ // +1
        private void m1(Acmec1 a, Acmec2 b, Acmec4 c, Acmec5 d){}
    }

    class Acmec4 {
        private void m1(Acmec2 a){}
    }

    class Acmec5 {
        private void m1(Acmec2 a){}
    }
}
