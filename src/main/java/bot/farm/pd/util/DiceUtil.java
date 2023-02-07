package bot.farm.pd.util;

import java.util.Arrays;
import java.util.Random;

public class DiceUtil {
    public static int[] roll5d6() {
        int[] arr = new int[5];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(1, 7);
        }
        Arrays.sort(arr);
        return arr;
    }

    public static void reroll(int[] firstRoll, int[] reroll) {
        Random random = new Random();
        for (int k : reroll) {
            for (int j = 0; j < firstRoll.length; j++) {
                if (firstRoll[j] == k) {
                    firstRoll[j] = random.nextInt(1, 7);
                    break;
                }
            }
        }
        Arrays.sort(firstRoll);
    }
}
