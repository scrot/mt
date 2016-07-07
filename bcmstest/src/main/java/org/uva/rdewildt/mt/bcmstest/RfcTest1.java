/**
 * Created by roy on 5/21/16.
 * <p>
 * RFC Test 1:
 * The union of all methods of a class and all the methods their invocations
 * <p>
 * Main = 7
 * ExternClass1 = 3
 */

public class RfcTest1 {

    public static class Main {
        // Object <init> invocation +1

        public Main() {
        } // <init> invocation +1


        public void Method1() { // +1
            System.out.println(Method2()); // +2
            ExternClass1.ExternMethod1(); // +1
            this.getClass(); //+1
        }

        // +0 (Already part of the set)
        String Method2() {
            return "method2";
        }
    }

    private static class ExternClass1 {
        // Object <init> invocation +1
        // <init> invocation +1
        static void ExternMethod1() {
        } // +1
    }
}
