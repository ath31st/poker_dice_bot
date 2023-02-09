package bot.farm.pd.repository;

import bot.farm.pd.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByIdChannelAndRoundTimeBetween(long idChannel, LocalDateTime roundTimeStart, LocalDateTime roundTimeEnd);

}
