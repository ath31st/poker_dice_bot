package bot.farm.pd.service;

import static bot.farm.pd.util.Command.REROLL;
import static bot.farm.pd.util.MessageEnum.ACTIVITY_CLEANING_TABLE;
import static bot.farm.pd.util.MessageEnum.ACTIVITY_FOR_GAME;
import static bot.farm.pd.util.MessageEnum.FINISH_ROUND;
import static bot.farm.pd.util.MessageEnum.START_ROUND;
import static bot.farm.pd.util.MessageEnum.TABLE_BUSY;

import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.entity.RoundResult;
import bot.farm.pd.util.DiceUtil;
import bot.farm.pd.util.RandomPhrase;
import bot.farm.pd.util.StringUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoundService {
  private final MessageService messageService;
  private final PlayerService playerService;
  private final ScoreService scoreService;
  private final StatService statService;
  private final ConcurrentMap<Long, PokerRound> rounds;

  public void startNewRound(MessageChannel channel, Long userInitiator) {
    if (rounds.containsKey(channel.getIdLong())) {
      messageService.sendMessage(channel, TABLE_BUSY.value);
      return;
    }

    Map<Long, PlayerInRound> players = new HashMap<>();

    String message = String.format(START_ROUND.value,
        StringUtil.diamondWrapperForId(userInitiator));

    PokerRound pr = PokerRound.builder()
        .players(players)
        .idChannel(channel.getIdLong())
        .playerInitiator(userInitiator)
        .startRound(LocalDateTime.now())
        .actionCounter(0)
        .isEnded(false)
        .build();

    rounds.put(channel.getIdLong(), pr);

    channel.getJDA().getPresence().setActivity(Activity.watching(ACTIVITY_FOR_GAME.value));
    messageService.sendMessage(channel, message);

  }

  public void rollDices(Message message) {
    Long chatId = message.getChannel().getIdLong();
    Long userId = message.getAuthor().getIdLong();

    if (checkRoundAvailable(chatId, userId)) {
      PokerRound pr = rounds.get(chatId);
      PlayerInRound pir = playerService.createPiR();
      pr.setActionCounter(pr.getActionCounter() + 2);

      String playerName = message.getMember().getNickname() == null ?
          message.getAuthor().getName() : message.getMember().getNickname();

      if (!playerService.existsPlayer(userId)) {
        playerService.saveNewPlayer(userId,
            message.getAuthor().getName(),
            playerName,
            message.getAuthor().getDiscriminator());
      } else {
        playerService.checkAndUpdateNickname(userId, playerName);
      }

      int[] rollDices = DiceUtil.roll5d6();

      pir.setName(playerName);
      pir.setDices(rollDices);
      pir.setRoll(false);
      pr.getPlayers().put(userId, pir);
      pr.setActionCounter(pr.getActionCounter() - 1);

      messageService.sendMessage(message.getChannel(),
          String.format(RandomPhrase.getRollDicesPhrase(),
              StringUtil.diamondWrapperForId(userId),
              StringUtil.resultWithBrackets(rollDices)));
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
            String.format(RandomPhrase.getRerollPhrase(),
                StringUtil.diamondWrapperForId(userId),
                StringUtil.resultWithBrackets(reroll),
                StringUtil.resultWithBrackets(firstRoll)));

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
          String.format(RandomPhrase.getPassPhrase(), StringUtil.diamondWrapperForId(userId)));

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

      message.getChannel().getJDA().getPresence()
          .setActivity(Activity.playing(ACTIVITY_CLEANING_TABLE.value));
      messageService.sendMessage(message.getChannel(),
          String.format(FINISH_ROUND.value, StringUtil.diamondWrapperForId(userId)));
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

  private boolean checkRoundAvailable(Long chatId, Long userId) {
    if (!rounds.containsKey(chatId)) return false;

    PokerRound pr = rounds.get(chatId);
    return !pr.isEnded() &&
        !pr.getPlayers().containsKey(userId);
  }

  private void checkAvailableActions(MessageChannel channel, PokerRound pr) {
    if (pr.getActionCounter() > 0) return;
    saveResultsAndDeleteRound(channel, pr);
  }

  public void saveResultsAndDeleteRound(MessageChannel channel, PokerRound pr) {
    channel.getJDA().getPresence().setActivity(Activity.playing(ACTIVITY_CLEANING_TABLE.value));
    Map<Long, RoundResult> result = scoreService.processingRoundResult(pr);

    messageService.sendResult(channel, result, pr.getPlayers());

    if (pr.getPlayers().size() > 1) {
      Long winner = result.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue(DiceUtil::customComparator))
          .findFirst()
          .orElseThrow()
          .getKey();

      statService.saveRoundResult(channel.getIdLong(), winner);
    }

    rounds.remove(pr.getIdChannel());
  }
}
