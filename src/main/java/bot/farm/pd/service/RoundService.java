package bot.farm.pd.service;

import bot.farm.pd.entity.Player;
import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.util.Command;
import bot.farm.pd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static bot.farm.pd.util.Command.*;

@Service
@RequiredArgsConstructor
public class RoundService {
    private final MessageService messageService;
    private final PlayerService playerService;
    private final ConcurrentHashMap<Long, PokerRound> rounds;

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (!content.startsWith("!") || content.length() > 200) return;

        if (content.startsWith(START.value)) {
            startNewRound(message.getChannel(), content);
        }
        if (content.startsWith(ROLL.value)) {
            rollDice(message);
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

    private void startNewRound(MessageChannel channel, String startCommand) {
        Pattern pattern = Pattern.compile("^" + START.value + " (<@[0-9]{18}>){2,}$");
        Matcher matcher = pattern.matcher(startCommand);
        Map<Long, Player> players = StringUtil.getPlayersId(startCommand).stream().collect(Collectors.toMap(Long::valueOf, v -> new Player()));

        if (matcher.matches() && players.size() > 1) {
            String message = "Начинается новый раунд покера с костями!\n"
                    + players.keySet().stream()
                    .map(p -> StringUtil.diamondWrapperForId(String.valueOf(p)))
                    .collect(Collectors.joining(" vs "));

            PokerRound pokerRound = new PokerRound();
            pokerRound.setPlayers(players);
            pokerRound.setIdChannel(channel.getIdLong());
            // todo think about cancel round other players
            rounds.put(channel.getIdLong(), pokerRound);

            messageService.sendMessage(channel, message);
        } else {
            messageService.sendMessage(channel, "Error!");
        }

    }

    private void rollDice(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (rounds.containsKey(chatId) && rounds.get(chatId).getPlayers().containsKey(userId)) {
            PokerRound pokerRound = rounds.get(chatId);
            Player player;

            if (playerService.existsPlayer(userId)) {
                player = playerService.getPlayerById(userId).get();
            } else {
                player = playerService.saveNewPlayer(userId,
                        message.getAuthor().getName(),
                        Objects.requireNonNull(message.getMember()).getNickname(),
                        message.getAuthor().getDiscriminator());
            }

            pokerRound.getPlayers().put(player.getId(), player);

        }
    }

    private void rerollDice(String reroll) {
        Pattern pattern = Pattern.compile("^" + REROLL.value + "( [0-9]){1,5}$");
        Matcher matcher = pattern.matcher(reroll);
    }
}
