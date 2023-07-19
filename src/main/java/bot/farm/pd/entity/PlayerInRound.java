package bot.farm.pd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
