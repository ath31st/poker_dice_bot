package bot.farm.pd.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "players")
public class Player {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String username;
    private String nickname;
    private String discriminator;

}
