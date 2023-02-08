package bot.farm.pd.service;

import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.entity.RoundResult;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    public void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public void sendResult(MessageChannel channel, Map<Long, RoundResult> result, Map<Long, PlayerInRound> players) {
        String message = "Результаты раунда:\n" +
                result.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(this::customComparator))
                        .map(p -> players.get(p.getKey()).getName() + ": " + p.getValue().getCombination().value +
                                " {" + p.getValue().getScore() + "}")
                        .collect(Collectors.joining("\n"));

        channel.sendMessage(message).queue();
    }

    private int customComparator(RoundResult r1, RoundResult r2) {
        if (r1.getPriority() < r2.getPriority()) {
            return 1;
        } else if (r1.getPriority() == r2.getPriority()) {
            return Integer.compare(r2.getScore(), r1.getScore());
        } else {
            return -1;
        }
    }
}
