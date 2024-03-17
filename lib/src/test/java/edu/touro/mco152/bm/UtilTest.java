package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {

    /**
     * Boundary Condition check - numeric overflows.
     * Checks the that randInt of the Util class correctly returns an int between the min and max
     * that can include both the min and the max and does not return less than the min or more than
     * the max.
     * <p> Also checks if the results are exactly Right (Right Bicep) by checking for the exact number in a
     * case where min equals max </p>
     */

    @Test
    void randIntTest(){
        int min = 5;
        int max = 7;
        for (int i = 0; i < 100; i++){
            int result = Util.randInt(min, max);
            assertTrue(result <= 10 && result >= 5);
        }
        min = 6;
        for (int i = 0; i < 100; i++){
            int result = Util.randInt(min, max);
            assertTrue(result <= 7 && result >= 6);
        }
        min = 7;
        int result = Util.randInt(min, max);
        assertEquals(7, result);
    }

}
