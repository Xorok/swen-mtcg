package at.technikum.apps.mtcg.util;

import java.util.Random;

public class RandomUtils {
    private final Random rand = new Random();

    public int getRandomNumber(int minIncl, int maxExcl) {
        return rand.nextInt(maxExcl - minIncl) + minIncl;
    }
}
