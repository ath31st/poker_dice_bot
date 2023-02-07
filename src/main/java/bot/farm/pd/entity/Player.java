package bot.farm.pd.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Player {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String username;
    private String nickname;
    private String discriminator;
}