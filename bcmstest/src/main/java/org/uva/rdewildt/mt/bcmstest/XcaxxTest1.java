public class XcaxxTest1 {
    class Xcaxx1 {
        private Xcaxx1 x1;
        private Xcaxx2 x2;
        private Xcaxx3 x3;
        private Xcaxx4 x4;
    }

    class Xcaxx2 extends Xcaxx1 {
        private Xcaxx1 x1;
    }

    class Xcaxx3 extends Xcaxx2 {
        private Xcaxx1 x1;
    }

    class Xcaxx4 {
        private Xcaxx1 x1;
        private int x2;
        private int x3;
    }
}
