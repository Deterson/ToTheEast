package websocket;

import decrypto.Game;
import decrypto.Player;
import decrypto.action.Action;
import decrypto.action.ActionChangeColor;
import decrypto.action.ActionCode;
import decrypto.action.ActionRename;
import org.codehaus.jackson.map.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;

public class DecryptoBroadcast
{
    public static void broadcastUpdateWhiteClues(Game game)
    {
        broadcast(game, DecryptoParser.gameUpdateWithoutWhiteClues(game));
    }

    public static void broadcastUpdate(Game game)
    {
        broadcast(game, DecryptoParser.gameUpdate(game));
    }

    public static void broadcastChangeColor(Game game, ActionChangeColor actionChangeColor)
    {
        try {
            broadcast(game, new ObjectMapper().writeValueAsString(actionChangeColor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastRename(Game game, ActionRename actionRename)
    {
        try {
            broadcast(game, new ObjectMapper().writeValueAsString(actionRename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(Game game, String message)
    {
        try {
            for (Player p : game.getPlayers())
                for (Session s : p.getWsSessions())
                    synchronized (s)
                    {
                        s.getBasicRemote().sendText(message);
                    }

        } catch (IOException e) { // TODO dans quelle situation ça se déclenche?
            e.printStackTrace();
        }
    }

    public static void sendCodes(Game game)
    {
        try {
            ActionCode wActionCode = new ActionCode(game.getWhiteCode());
            ActionCode bActionCode = new ActionCode(game.getBlackCode());
            for (Session s : game.getWhiteCluer().getWsSessions())
                synchronized (s) {
                    s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(wActionCode));
                }

            for (Session s : game.getBlackCluer().getWsSessions())
                synchronized (s) {
                s.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(bActionCode));
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
