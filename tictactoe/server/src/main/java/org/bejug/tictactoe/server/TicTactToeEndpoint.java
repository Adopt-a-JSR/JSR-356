package org.bejug.tictactoe.server;

import java.io.IOException;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author johan
 * @author mike
 */
@ServerEndpoint("/endpoint")
public class TicTactToeEndpoint {
    private static final Logger LOGGER = Logger.getLogger("tictactoe");
    private static final String GAME_PROPERTY_KEY = "Game";
    private static final int MAX_IDLE_TIMEOUT = 1000;

    @OnOpen
    public void onOpen(final Session session) throws IOException {
        LOGGER.info("Opening Endpoint for session " + session);
        session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);

        final Game lastGame = InMemoryGameRegistry.getInstance().getAvailableGame();
        session.getUserProperties().put(GAME_PROPERTY_KEY, lastGame);
        String sid = session.getId();

        Player player = new Player(sid, session);
        if (lastGame.getPlayer1() == null) {
            lastGame.setPlayer1(player);
            session.getBasicRemote().sendText(TicTactToeMessage.JOINED_AS_FIRST_PLAYER.getMessage());
            LOGGER.fine("First player joined, message send back");
        } else {
            lastGame.setPlayer2(player);
            session.getBasicRemote().sendText(TicTactToeMessage.JOINED_AS_LAST_PLAYER.getMessage());
            LOGGER.fine("Second player joined, message send back");
            lastGame.getPlayer1().getSession().getBasicRemote().sendText(TicTactToeMessage.SECOND_PLAYER_JOINED.getMessage());
            LOGGER.fine("Second player joined, first player notified");
        }
    }

    @OnClose
// bug?	public void onClose (Session session, CloseReason reason) {
    public void onClose(final Session session) {

        LOGGER.info("CLOSED! " + session);
        //	LOGGER.info("reason: "+reason.getCloseCode()+ "-- "+reason.getReasonPhrase());
    }

    @OnMessage
    public void onMessage(final String obj, final Session session) throws IOException {
        LOGGER.info("Got message from session " + session + ": " + obj);
        Game game = (Game) session.getUserProperties().get(GAME_PROPERTY_KEY);
        Player player = game.getPlayerForSessionId(session.getId());
        if (player != null) {
            player.getSession().getBasicRemote().sendText(obj);
        }
    }


}
