package bot.farm.pd.repository;

import bot.farm.pd.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Override
    boolean existsById(@NotNull Long aLong);


}
