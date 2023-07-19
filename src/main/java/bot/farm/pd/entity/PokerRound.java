package bot.farm.pd.entity;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PokerRound {
  private Long playerInitiator;
  private boolean isEnded;
  private Long idChannel;
  private Map<Long, PlayerInRound> players;
  private LocalDateTime startRound;
  private int actionCounter;
}
