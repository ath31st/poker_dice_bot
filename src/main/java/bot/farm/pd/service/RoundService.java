package bot.farm.pd.service;

import bot.farm.pd.util.Command;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bot.farm.pd.util.Command.REROLL;
import static bot.farm.pd.util.Command.START;

@Service
@RequiredArgsConstructor
public class RoundService {
    private final MessageService messageService;

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.equals("!ping")) {
            messageService.sendMessage(event.getChannel(),"Pong!");
        }
    }

    private boolean checkOccurrence(String command) {
        if (!command.startsWith("/") || command.length() > 200) return false;

        Pattern pattern = null;
        if (command.startsWith(START.value)) {
            pattern = Pattern.compile("^" + START.value + " (<@[0-9]{18}>){2,}$");
        } else if (command.startsWith(REROLL.value)) {
            pattern = Pattern.compile("^" + REROLL.value + "( [0-9]){1,5}$");
        }
        if (pattern != null) {
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) return true;
        }

        return Arrays.stream(Command.values()).anyMatch(c -> c.value.startsWith(command));
    }
}
