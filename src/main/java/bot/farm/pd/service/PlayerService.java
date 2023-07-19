package bot.farm.pd.service;

import bot.farm.pd.entity.Player;
import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.repository.PlayerRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
  private final PlayerRepository playerRepository;

  @Transactional
  public void saveNewPlayer(Long id, String username, String nickname, String discriminator) {
    Player p = Player.builder()
        .id(id)
        .username(username)
        .nickname(nickname)
        .discriminator(discriminator)
        .build();

    playerRepository.save(p);
  }

  public Optional<Player> getPlayerById(Long userId) {
    return playerRepository.findById(userId);
  }

  public boolean existsPlayer(Long userId) {
    return playerRepository.existsById(userId);
  }

  public void checkAndUpdateNickname(Long userId, String nickname) {
    if (playerRepository.existsByIdAndNickname(userId, nickname)) return;

    playerRepository.updateNicknameById(nickname, userId);
  }

  public PlayerInRound createPiR() {
    return PlayerInRound.builder()
        .isRoll(true)
        .isPass(true)
        .isReroll(true)
        .dices(new int[5])
        .score(0)
        .build();
  }
}
