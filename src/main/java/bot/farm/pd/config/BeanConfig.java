package bot.farm.pd.config;

import bot.farm.pd.entity.PokerRound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BeanConfig {
    @Bean
    public ConcurrentHashMap<Long, PokerRound> rounds() {
        return new ConcurrentHashMap<>();
    }

}
