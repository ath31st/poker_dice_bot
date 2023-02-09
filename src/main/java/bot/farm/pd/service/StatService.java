package bot.farm.pd.service;

import bot.farm.pd.entity.Result;
import bot.farm.pd.repository.ResultRepository;
import bot.farm.pd.util.Help;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatService {
    private final ResultRepository resultRepository;
    private final MessageService messageService;
    private final PlayerService playerService;


    public void printHelpMessage(Message message) {
        messageService.sendMessage(message.getChannel(), Help.HELP.value);
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
                "Таблица лидеров недели этого канала (Топ 5):\n" +
                leaders.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .map(p -> p.getKey() + ": {" + p.getValue() + "}")
                        .collect(Collectors.joining("\n")) + "```";

        messageService.sendMessage(channel, message);
    }
}
