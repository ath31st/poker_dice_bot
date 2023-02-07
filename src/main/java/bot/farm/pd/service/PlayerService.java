package bot.farm.pd.service;

import bot.farm.pd.entity.Player;
import bot.farm.pd.entity.PlayerInRound;
import bot.farm.pd.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player saveNewPlayer(Long id, String username, String nickname, String discriminator) {
        Player p = Player.builder()
                .id(id)
                .username(username)
                .nickname(nickname)
                .discriminator(discriminator)
                .build();

        return playerRepository.save(p);
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public boolean existsPlayer(Long id) {
        return playerRepository.existsById(id);
    }

    public PlayerInRound createPiR() {
        return PlayerInRound.builder()
                .isRoll(true)
                .isPass(true)
                .isReroll(true)
                .dices(new int[5])
                .build();
    }
}
