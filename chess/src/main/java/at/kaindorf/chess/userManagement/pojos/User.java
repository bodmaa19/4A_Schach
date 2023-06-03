package at.kaindorf.chess.userManagement.pojos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"password" ,"bestScore"})
@ToString(exclude = {})
@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
    private int userId;
    private String username;
    private String password;
    @JsonAlias(value = "best_score")
    private Integer bestScore;
}
