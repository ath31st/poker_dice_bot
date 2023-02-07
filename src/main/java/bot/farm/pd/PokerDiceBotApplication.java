package bot.farm.pd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PokerDiceBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokerDiceBotApplication.class, args);
    }

}
