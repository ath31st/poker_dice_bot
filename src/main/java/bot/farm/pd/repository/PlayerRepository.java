package bot.farm.pd.repository;

import bot.farm.pd.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
  @Transactional
  @Modifying
  @Query("update Player p set p.nickname = ?1 where p.id = ?2")
  void updateNicknameById(String nickname, Long id);

  boolean existsByIdAndNickname(Long id, String nickname);

  @Override
  boolean existsById(@NotNull Long aLong);
}
