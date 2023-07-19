package bot.farm.pd.scheduler;

import static bot.farm.pd.util.MessageEnum.ACTIVITY_CLEANING_TABLE;
import static bot.farm.pd.util.MessageEnum.TIME_EXPIRED;

import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.service.MessageService;
import bot.farm.pd.service.RoundService;
import bot.farm.pd.util.RandomPhrase;
import bot.farm.pd.util.StringUtil;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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
        jda.getPresence().setActivity(Activity.playing(ACTIVITY_CLEANING_TABLE.value));

        MessageChannel channel = jda.getChannelById(MessageChannel.class, entry.getKey());
        if (channel != null) {
          messageService.sendMessage(channel, TIME_EXPIRED.value);

          PokerRound pr = entry.getValue();
          pr.getPlayers().entrySet()
              .stream()
              .filter(e -> e.getValue().isReroll() && e.getValue().isPass())
              .forEach(e -> messageService.sendMessage(channel,
                  String.format(RandomPhrase.getAutoPassPhrase(), StringUtil.diamondWrapperForId(e.getKey()))));
          roundService.saveResultsAndDeleteRound(channel, pr);
        } else {
          iterator.remove();
        }
      }
    }
  }
}
