package bot.farm.pd.listener;

import bot.farm.pd.service.RoundService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Component;

import static bot.farm.pd.util.Command.*;

@Component
@RequiredArgsConstructor
public class RoundListener {

    private final RoundService roundService;

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (!content.startsWith("!") || content.length() > 200) return;

        if (content.equals(START.value)) {
            roundService.startNewRound(message.getChannel(), message.getAuthor().getIdLong());
        }
        if (content.equals(ROLL.value)) {
            roundService.rollDices(message);
        }
        if (content.startsWith(REROLL.value)) {
            roundService.rerollDices(message);
        }
        if (content.equals(PASS.value)) {
            roundService.pass(message);
        }
        if (content.equals(FINISH.value)) {
            roundService.finishRound(message);
        }
    }
}
