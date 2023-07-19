package bot.farm.pd.service;

import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.entity.RoundResult;
import bot.farm.pd.util.DiceUtil;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

  public void sendMessage(MessageChannel channel, String message) {
    channel.sendMessage(message).queue();
  }

  public void sendResult(MessageChannel channel, Map<Long, RoundResult> result, Map<Long, PlayerInRound> players) {

    String message = "=====================\n" +
        "Результаты раунда:\n" + "```" +
        result.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(DiceUtil::customComparator))
            .map(p -> players.get(p.getKey()).getName() + ": " + p.getValue().getCombination().value +
                " {" + p.getValue().getScore() + "}")
            .collect(Collectors.joining("\n")) + "```";

    channel.sendMessage(message).queue();
  }
}
