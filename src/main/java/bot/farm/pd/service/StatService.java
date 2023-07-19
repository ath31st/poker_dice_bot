package bot.farm.pd.service;

import static bot.farm.pd.util.MessageEnum.COMBINATION;
import static bot.farm.pd.util.MessageEnum.HELP;

import bot.farm.pd.entity.Result;
import bot.farm.pd.repository.ResultRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatService {
  private final ResultRepository resultRepository;
  private final MessageService messageService;
  private final PlayerService playerService;


  public void printHelpMessage(Message message) {
    messageService.sendMessage(message.getChannel(), HELP.value);
  }

  public void printComboMessage(Message message) {
    messageService.sendMessage(message.getChannel(), COMBINATION.value);
  }

  @Transactional
  public void saveRoundResult(Long channelId, Long idWinner) {
    Result result = Result.builder()
        .idChannel(channelId)
        .player(playerService.getPlayerById(idWinner).get())
        .roundTime(LocalDateTime.now())
        .build();

    resultRepository.save(result);
  }

  @Transactional
  public void getLeaderBoardByChannel(MessageChannel channel) {
    List<Result> results = resultRepository.findByIdChannelAndRoundTimeBetween(channel.getIdLong(),
        LocalDateTime.now().minusDays(7), LocalDateTime.now());
    Map<String, Long> leaders = results.stream()
        .collect(Collectors.groupingBy(p -> p.getPlayer().getNickname(), Collectors.counting()));

    String message = "=====================\n" + "```" +
        "За прошедшую неделю сыграно: " + results.size() + " раунда(ов).\n" +
        "Таблица лидеров этого канала (Топ 5):\n" +
        leaders.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .map(p -> p.getKey() + ": {" + p.getValue() + "}")
            .collect(Collectors.joining("\n")) + "```";

    messageService.sendMessage(channel, message);
  }
}
