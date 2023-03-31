package at.kaindorf.chess.websocket;

import at.kaindorf.chess.pojos.ChessReturn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerMessage {
    private String username1;
    private String username2;
    private String fenString;
}