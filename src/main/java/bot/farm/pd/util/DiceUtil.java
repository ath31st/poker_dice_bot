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
}
