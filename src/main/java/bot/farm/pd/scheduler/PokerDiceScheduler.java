package bot.farm.pd.scheduler;

import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.service.MessageService;
import bot.farm.pd.service.RoundService;
import bot.farm.pd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class PokerDiceScheduler {
    @Value("${round.duration}")
    private int duration;
    private final JDA jda;
    private final MessageService messageService;
    private final RoundService roundService;
    private final ConcurrentHashMap<Long, PokerRound> rounds;

    @Scheduled(fixedDelay = 15000)
    public void finalizeRounds() {
        Iterator<Map.Entry<Long, PokerRound>> iterator = rounds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, PokerRound> entry = iterator.next();
            if (entry.getValue().getStartRound().plusMinutes(duration).isBefore(LocalDateTime.now())) {
                jda.getPresence().setActivity(Activity.playing("уборку игрового стола"));

                MessageChannel channel = jda.getChannelById(MessageChannel.class, entry.getKey());
                if (channel != null) {
                    messageService.sendMessage(channel, "Время раунда подошло к концу");

                    PokerRound pr = entry.getValue();
                    pr.getPlayers().entrySet()
                            .stream()
                            .filter(e -> e.getValue().isReroll() && e.getValue().isPass())
                            .forEach(e -> messageService.sendMessage(channel, "Угадайте, что снится " +
                                    StringUtil.diamondWrapperForId(e.getKey()) +
                                            "? Автоматический пропуск хода! Принесите ему(ей) одеяло"));
                    roundService.saveResultsAndDeleteRound(channel, pr);
                } else {
                    iterator.remove();
                }
            }
        }
    }
}
