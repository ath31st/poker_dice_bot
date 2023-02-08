package bot.farm.pd.service;

import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.entity.RoundResult;
import bot.farm.pd.util.DiceUtil;
import bot.farm.pd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static bot.farm.pd.util.Command.REROLL;
import static bot.farm.pd.util.Command.START;

@Service
@RequiredArgsConstructor
public class RoundService {
    private final MessageService messageService;
    private final PlayerService playerService;
    private final ScoreService scoreService;
    private final ConcurrentHashMap<Long, PokerRound> rounds;

    public void startNewRound(MessageChannel channel, String startCommand, Long userInitiator) {
        if (rounds.containsKey(channel.getIdLong())) {
            messageService.sendMessage(channel, "Извините, игровой стол сейчас занят");
            return;
        }

        Pattern pattern = Pattern.compile("^" + START.value + " (<@[0-9]{18}>\\s?){2,}$");
        Matcher matcher = pattern.matcher(startCommand);

        if (!matcher.matches()) return;

        Map<Long, PlayerInRound> players = StringUtil.getPlayersId(startCommand).stream()
                .collect(Collectors.toMap(Long::valueOf, v -> playerService.createPiR()));

        if (players.size() <= 1) return;

        String message = "Начинается новый раунд покера с костями!\n"
                + players.keySet().stream()
                .map(StringUtil::diamondWrapperForId)
                .collect(Collectors.joining(" vs "));

        PokerRound pr = PokerRound.builder()
                .players(players)
                .idChannel(channel.getIdLong())
                .playerInitiator(userInitiator)
                .startRound(LocalDateTime.now())
                .actionCounter(players.size() * 2)
                .isEnded(false)
                .build();

        rounds.put(channel.getIdLong(), pr);

        messageService.sendMessage(channel, message);

    }

    public void rollDices(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (checkRollAvailable(chatId, userId)) {
            PokerRound pr = rounds.get(chatId);
            PlayerInRound pir = pr.getPlayers().get(userId);
            String playerName = message.getMember().getNickname() == null ?
                    message.getAuthor().getName() : message.getMember().getNickname();

            if (!playerService.existsPlayer(userId)) {
                playerService.saveNewPlayer(userId,
                        message.getAuthor().getName(),
                        Objects.requireNonNull(message.getMember()).getNickname(),
                        message.getAuthor().getDiscriminator());
            }

            int[] rollDices = DiceUtil.roll5d6();

            pir.setName(playerName);
            pir.setDices(rollDices);
            pir.setRoll(false);
            pr.getPlayers().put(userId, pir);
            pr.setActionCounter(pr.getActionCounter() - 1);

            messageService.sendMessage(message.getChannel(),
                    StringUtil.diamondWrapperForId(userId) + " ловко бросает кости " +
                            StringUtil.resultWithBrackets(rollDices));
        }
    }

    public void rerollDices(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (checkRerollOrPassAvailable(chatId, userId)) {
            Pattern pattern = Pattern.compile("^" + REROLL.value + "(\\s+[1-6]){1,5}$");
            Matcher matcher = pattern.matcher(message.getContentRaw());

            if (matcher.matches()) {
                PokerRound pr = rounds.get(chatId);
                PlayerInRound pir = pr.getPlayers().get(userId);

                int[] reroll = StringUtil.getRerollNumbers(message.getContentRaw());
                int[] firstRoll = pir.getDices();
                DiceUtil.reroll(firstRoll, reroll);

                pir.setDices(firstRoll);
                pir.setReroll(false);
                pir.setPass(false);
                pr.getPlayers().put(userId, pir);
                pr.setActionCounter(pr.getActionCounter() - 1);

                messageService.sendMessage(message.getChannel(),
                        StringUtil.diamondWrapperForId(userId) +
                                " перебрасывает кости " +
                                StringUtil.resultWithBrackets(reroll) + "\n" +
                                "Получилось " + StringUtil.resultWithBrackets(firstRoll));

                checkAvailableActions(message.getChannel(), pr);
            }
        }
    }

    public void pass(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (checkRerollOrPassAvailable(chatId, userId)) {
            PokerRound pr = rounds.get(chatId);
            PlayerInRound pir = pr.getPlayers().get(userId);

            pir.setReroll(false);
            pir.setPass(false);
            pr.getPlayers().put(userId, pir);
            pr.setActionCounter(pr.getActionCounter() - 1);

            messageService.sendMessage(message.getChannel(),
                    StringUtil.diamondWrapperForId(userId) + " с ухмылкой пропускает ход");

            checkAvailableActions(message.getChannel(), pr);
        }
    }

    public void finishRound(Message message) {
        Long chatId = message.getChannel().getIdLong();
        Long userId = message.getAuthor().getIdLong();

        if (rounds.containsKey(chatId) && rounds.get(chatId).getPlayerInitiator().equals(userId)) {
            PokerRound pokerRound = rounds.get(chatId);
            pokerRound.setEnded(true);

            rounds.remove(chatId);

            messageService.sendMessage(message.getChannel(),
                    StringUtil.diamondWrapperForId(userId) + " досрочно завершает раунд, результаты будут аннулированы");
        }
    }

    private boolean checkRerollOrPassAvailable(Long chatId, Long userId) {
        if (!rounds.containsKey(chatId)) return false;

        PokerRound pr = rounds.get(chatId);
        return pr.getPlayers().containsKey(userId) &&
                !pr.isEnded() &&
                !pr.getPlayers().get(userId).isRoll() &&
                pr.getPlayers().get(userId).isReroll() &&
                pr.getPlayers().get(userId).isPass();
    }

    private boolean checkRollAvailable(Long chatId, Long userId) {
        if (!rounds.containsKey(chatId)) return false;

        PokerRound pr = rounds.get(chatId);
        return !pr.isEnded() &&
                pr.getPlayers().containsKey(userId) &&
                pr.getPlayers().get(userId).isRoll();
    }

    private void checkAvailableActions(MessageChannel channel, PokerRound pr) {
        if (pr.getActionCounter() > 0) return;
        saveResultsAndDeleteRound(channel, pr);
    }

    private void saveResultsAndDeleteRound(MessageChannel channel, PokerRound pr) {
        //todo save result!
        Map<Long, RoundResult> result = scoreService.processingRoundResult(pr);
        rounds.remove(pr.getIdChannel());
        messageService.sendResult(channel, result, pr.getPlayers());
    }
}
