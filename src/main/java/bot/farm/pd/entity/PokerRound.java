package bot.farm.pd.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PokerRound {
    private Long idChannel;
    private Map<Long, Player> players;

}
