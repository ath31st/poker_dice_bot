package bot.farm.pd.repository;

import bot.farm.pd.entity.Result;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
  List<Result> findByIdChannelAndRoundTimeBetween(
      long idChannel, LocalDateTime roundTimeStart, LocalDateTime roundTimeEnd);
}
