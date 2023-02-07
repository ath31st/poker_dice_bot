package bot.farm.pd.scheduler;

import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.service.MessageService;
import bot.farm.pd.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class PokerDiceScheduler {
    private final MessageService messageService;
    private final ConcurrentHashMap<Long, PokerRound> rounds;

}
