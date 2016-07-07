public class XcmxxTest1 {
    class Xcmxx1 {
        private void m1(Xcmxx1 a, Xcmxx2 b, Xcmxx3 c, Xcmxx4 d) {
        }
    }

    class Xcmxx2 extends Xcmxx1 {
        private void m1(Xcmxx1 a) {
        }
    }

    class Xcmxx3 extends Xcmxx2 {
        private void m1(Xcmxx1 a, Xcmxx2 b) {
        }
    }

    class Xcmxx4 {
        private void m1(Xcmxx1 a) {
        }
    }
}
