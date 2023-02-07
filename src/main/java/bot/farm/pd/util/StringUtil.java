package bot.farm.pd.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StringUtil {

    public static String getIdFromBrackets(String userId) {
        return userId.replaceAll("<@|>"," ").trim().replaceAll("\\s+", " ");
    }
    public static String diamondWrapperForId(String userId) {
        return "<@" + userId + ">";
    }
    public static Set<String> getPlayersId(String command) {

        return Arrays.stream(command.substring(command.indexOf(" "))
                .replaceAll("<@|>"," ")
                .trim()
                .replaceAll("\\s+", " ")
                .split(" "))
                .collect(Collectors.toSet());
    }
}
