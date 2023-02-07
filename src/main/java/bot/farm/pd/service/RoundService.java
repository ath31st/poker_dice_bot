package bot.farm.pd.service;

import bot.farm.pd.entity.Player;
import bot.farm.pd.util.Command;
import bot.farm.pd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        if (!content.startsWith("!") || content.length() > 200) return;

        if (content.startsWith(START.value)) {
            startNewRound(message.getChannel(), content, event.getAuthor().getId());
        }
    }

    private boolean checkOccurrence(String command) {
        if (!command.startsWith("!") || command.length() > 200) return false;

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

    private void startNewRound(MessageChannel channel, String startCommand, String userId) {
        Pattern pattern = Pattern.compile("^" + START.value + " (<@[0-9]{18}>){2,}$");
        Matcher matcher = pattern.matcher(startCommand);
        Set<String> players = StringUtil.getPlayersId(startCommand);

        if (matcher.matches() && players.size() > 1) {
            String message = "Начинается новый раунд покера с костями!\n"
                    + players.stream()
                    .map(StringUtil::diamondWrapperForId)
                    .collect(Collectors.joining(" vs "));

            messageService.sendMessage(channel, message);
        } else {
            messageService.sendMessage(channel, "Error!");
        }

    }
}
