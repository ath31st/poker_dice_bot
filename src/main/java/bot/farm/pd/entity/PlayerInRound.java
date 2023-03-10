package bot.farm.pd.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PlayerInRound {
    private String name;
    private boolean isRoll;
    private boolean isReroll;
    private boolean isPass;
    private int[] dices;
    private int score;

}
