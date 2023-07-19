package bot.farm.pd.entity;

import bot.farm.pd.util.Combination;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundResult {
  private int score;
  private Combination combination;
  private int priority;
}
