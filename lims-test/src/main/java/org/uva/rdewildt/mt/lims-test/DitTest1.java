import java.util.ArrayList;

/**
 * Created by roy on 5/21/16.
 *
 * DIT Test 1:
 * maximum number of steps from the class node to the root of the tree
 *
 * Super = 1
 * Child = 2
 * Infant = 3
 * Interface = 1
 * Implementation = 1
 * NewList =
 */
public class DitTest1 {
    abstract class Super{}
    abstract class Child extends Super{}
    class Infant extends Child{}

    interface Interface{}
    class Implementation implements Interface{}

    class NewList extends ArrayList {}
}
