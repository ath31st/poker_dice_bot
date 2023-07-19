package bot.farm.pd.config;

import bot.farm.pd.entity.PokerRound;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BeanConfig {
  @Bean
  public ConcurrentHashMap<Long, PokerRound> rounds() {
    return new ConcurrentHashMap<>();
  }

}
