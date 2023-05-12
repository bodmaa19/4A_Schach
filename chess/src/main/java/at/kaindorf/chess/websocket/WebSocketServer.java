package at.kaindorf.chess.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebSocketServer {

    private List<GenericStack<String>> gameLobbies = new ArrayList<>();
    private GenericStack<String> mainLobby = new GenericStack<>(100);
    private Map<String, String> usernameSessionMap = new HashMap<>();

    @MessageMapping("/send/moves")
    @SendTo("/sub/moves")
    public ServerMessage sendBoard(String fenString, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        try {
            GenericStack<String> lobby = getBothSessions(sessionId);
            String username1 = lobby.getList().get(0);
            String username2 = lobby.getList().get(1);

            ServerMessage message = new ServerMessage(username1, username2, fenString);
            return message;
        } catch (NoSuchObjectException | IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/send/sessionUsernameMap")
    public void bindSessionUsername(String username, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        if(!usernameSessionMap.containsKey(username)) {
            usernameSessionMap.put(sessionId, username);
        }
    }

    private GenericStack<String> getBothSessions(String sessionID) throws NoSuchObjectException {
        for (GenericStack<String> lobbies : gameLobbies) {
            if(lobbies.containsElement(sessionID)) {
                return lobbies;
            }
        }

        throw new NoSuchObjectException("Dieses Game exisitert nicht");
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        mainLobby.add(sessionId);
        if(mainLobby.getNumberOfElements() >= 2) {
            GenericStack<String> lobby = new GenericStack(2);
            String conn1 = mainLobby.get();
            String conn2 = mainLobby.get();

            lobby.add(conn1);
            lobby.add(conn2);
            gameLobbies.add(lobby);
        }

        System.out.println("MainLobby: " + mainLobby);
        System.out.println("GameLobbies: " + gameLobbies);
    }
}
