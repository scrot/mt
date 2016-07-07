/**
 * Created by roy on 5/21/16.
 * <p>
 * WMC Test 1:
 * Weighted methods per class based on cyclomatic complexity
 * <p>
 * WMCTest1 = 16
 */
public class WmcTest1 {
    public WmcTest1() throws Exception { // +2

        int x = 0;
        int y = 1;

        switch (x) {
            case 1:
                break; // +1
            case 2:
                break; // +1
            case 4:
                break; // +1
            case 5:
                break; // +1
            default:
                break; // +1
        }

        if (x == 1 || y == 0) { // +1

        } else if (x == 0 && y == 1) { // +1

        } else { // +1

        }

        try {
            throw new Exception(); // +1

        } catch (Exception e) { // +1

        }

        do { // +1
            x++;
        } while (x < 10);

        while (x > 0) { // +1
            x--;
        }

        int z = x > 0 ? 1 : 0; // +2
    }
}
