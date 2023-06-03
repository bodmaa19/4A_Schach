package at.kaindorf.chess.userManagement.pojos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"bestScore"})
@ToString(exclude = {})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData
{
    private int userId;
    private String username;
    @JsonAlias(value = "best_score")
    private Integer bestScore;
    private String token;
}
