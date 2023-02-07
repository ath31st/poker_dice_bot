package bot.farm.pd.config;

import bot.farm.pd.service.RoundService;
import bot.farm.pd.service.StatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfig {
    private final RoundService roundService;
    private final StatService statService;
    @Value("${bot.token}")
    private String token;

    @Bean
    JDA jda() {
        return JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .setEventManager(new AnnotatedEventManager())
                .addEventListeners(roundService, statService)
                .build();
    }
}
