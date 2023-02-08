package bot.farm.pd.service;

import bot.farm.pd.util.Help;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Service;

import static bot.farm.pd.util.Command.HELP;

@Service
@RequiredArgsConstructor
public class StatService {
    private final MessageService messageService;

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (!content.startsWith("!") || content.length() > 200) return;

        if (content.equals(HELP.value)) {
            messageService.sendMessage(event.getChannel(), Help.HELP.value);
        }
    }
}
