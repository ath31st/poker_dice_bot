package bot.farm.pd.service;

import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.util.Command;
import bot.farm.pd.util.DiceUtil;
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
            rollDices(message);
        }
        if (content.startsWith(REROLL.value)) {
            rerollDices(message);
        }
    }

    private boolean checkOccurrence(String command) {
        if (!command.startsWith("!") || command.length() > 200) return false;
        return Arrays.stream(Command.values()).anyMatch(c -> c.value.startsWith(command));
    }

    private void startNewRound(MessageChannel channel, String startCommand) {
        Pattern pattern = Pattern.compile("^" + START.value + " (<@[0-9]{18}>){2,}$");
        Matcher matcher = pattern.matcher(startCommand);

        Map<Long, PlayerInRound> players = StringUtil.getPlayersId(startCommand).stream()
                .collect(Collectors.toMap(Long::valueOf, v -> playerService.createPiR()));

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

    private void rollDices(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (rounds.containsKey(chatId) && rounds.get(chatId).getPlayers().containsKey(userId) &&
                rounds.get(chatId).getPlayers().get(userId).isRoll()) {
            PokerRound pokerRound = rounds.get(chatId);
            PlayerInRound pir = pokerRound.getPlayers().get(userId);

            if (!playerService.existsPlayer(userId)) {
                playerService.saveNewPlayer(userId,
                        message.getAuthor().getName(),
                        Objects.requireNonNull(message.getMember()).getNickname(),
                        message.getAuthor().getDiscriminator());
            }

            int[] rollDices = DiceUtil.roll5d6();

            pir.setDices(rollDices);
            pir.setRoll(false);
            pokerRound.getPlayers().put(userId, pir);

            messageService.sendMessage(message.getChannel(),
                    Objects.requireNonNull(message.getMember()).getNickname() + " ловко бросает кости " +
                            StringUtil.resultWithBrackets(rollDices));
        }
    }

    private void rerollDices(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (rounds.containsKey(chatId) && rounds.get(chatId).getPlayers().containsKey(userId) &&
                rounds.get(chatId).getPlayers().get(userId).isReroll()) {
            Pattern pattern = Pattern.compile("^" + REROLL.value + "( [1-6]){1,5}$");
            Matcher matcher = pattern.matcher(message.getContentRaw());

            if (matcher.matches()) {
                PokerRound pokerRound = rounds.get(chatId);
                PlayerInRound pir = pokerRound.getPlayers().get(userId);

                int[] reroll = StringUtil.getRerollNumbers(message.getContentRaw());
                int[] firstRoll = pir.getDices();
                DiceUtil.reroll(firstRoll, reroll);

                pir.setDices(firstRoll);
                pir.setReroll(false);
                pir.setPass(false);
                pokerRound.getPlayers().put(userId, pir);

                messageService.sendMessage(message.getChannel(),
                        Objects.requireNonNull(message.getMember()).getNickname() +
                                " перебрасывает кости " +
                                StringUtil.resultWithBrackets(reroll) + "\n" +
                                "Получилось " + StringUtil.resultWithBrackets(firstRoll));
            }
        }

    }
}
