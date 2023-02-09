package bot.farm.pd.listener;

import bot.farm.pd.service.StatService;
import bot.farm.pd.util.Help;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Component;

import static bot.farm.pd.util.Command.HELP;
import static bot.farm.pd.util.Command.STATISTICS;

@Component
@RequiredArgsConstructor
public class StatListener {
    private final StatService statService;

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (!content.startsWith("!") || content.length() > 200) return;

        if (content.equals(HELP.value)) {
            statService.printHelpMessage(message);
        }

        if (content.equals(STATISTICS.value)) {
            statService.getLeaderBoardByChannel(message.getChannel());
        }
    }
}
