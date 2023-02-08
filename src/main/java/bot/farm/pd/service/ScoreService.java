package bot.farm.pd.service;

import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.entity.PokerRound;
import bot.farm.pd.entity.RoundResult;
import bot.farm.pd.util.Combination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static bot.farm.pd.util.DiceUtil.*;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final MessageService messageService;

    public void processingRoundResult(PokerRound pr) {
        Map<Long, PlayerInRound> players = pr.getPlayers();
        Map<Long, RoundResult> result = new HashMap<>();
        players.forEach((key, value) -> result.put(key, getRoundResult(value.getDices())));
    }

    private RoundResult getRoundResult(int[] dices) {
        RoundResult rr = new RoundResult();

        if (isPoker(dices)) {
            rr.setScore(dices[0] * 5);
            rr.setCombination(Combination.POKER);
            rr.setPriority(Combination.POKER.priority);
            return rr;
        } else if (isSequence(dices, 4)) {
            rr.setScore(dices[2] * 4);
            rr.setCombination(Combination.SQUARE);
            rr.setPriority(Combination.SQUARE.priority);
            return rr;
        } else if (isFullHouse(dices)) {
            rr.setScore(Arrays.stream(dices).sum());
            rr.setCombination(Combination.FULL_HOUSE);
            rr.setPriority(Combination.FULL_HOUSE.priority);
            return rr;
        } else if (isLargeStraight(dices)) {
            rr.setScore(Arrays.stream(dices).sum());
            rr.setCombination(Combination.LARGE_STRAIGHT);
            rr.setPriority(Combination.LARGE_STRAIGHT.priority);
            return rr;
        } else if (isSmallStraight(dices)) {
            rr.setScore(Arrays.stream(dices).sum());
            rr.setCombination(Combination.SMALL_STRAIGHT);
            rr.setPriority(Combination.SMALL_STRAIGHT.priority);
            return rr;
        } else if (isSequence(dices, 3)) {
            rr.setScore(sequenceScore(dices));
            rr.setCombination(Combination.SET);
            rr.setPriority(Combination.SET.priority);
            return rr;
        }
    }


}
