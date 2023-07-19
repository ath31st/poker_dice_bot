package bot.farm.pd.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@Table(name = "players")
public class Player {
  @Id
  @Column(name = "id", nullable = false)
  private Long id;
  private String username;
  private String nickname;
  private String discriminator;
  @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
  private List<Result> results;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(id, player.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
