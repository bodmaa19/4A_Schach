package at.kaindorf.chess.userManagement.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 21.05.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private int playerId;
    private User user;
}
