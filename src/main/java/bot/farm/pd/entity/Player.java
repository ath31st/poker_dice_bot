package bot.farm.pd.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
    @OneToMany(mappedBy = "player",fetch = FetchType.LAZY)
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
