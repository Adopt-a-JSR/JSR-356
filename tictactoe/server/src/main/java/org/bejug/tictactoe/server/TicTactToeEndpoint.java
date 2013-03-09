package org.bejug.tictactoe.server;

import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * A web-socket server endpoint for Tic-Tac-Toe games. This endpoint manages games through a
 * {@link org.bejug.tictactoe.server.GameRegistry}.
 *
 * @author johan
 * @author mike
 */
@ServerEndpoint("/endpoint")
public class TicTactToeEndpoint {
    /**
     * Name for storing a game in the session's user properties.
     */
    static final String GAME_PROPERTY_KEY = "Game";

    /**
     * Default web-socket timeout value.
     */
    static final int MAX_IDLE_TIMEOUT = 1000;

    /**
     * The class logger.
     */
    private static final Logger LOGGER = Logger.getLogger("tictactoe");

    /**
     * The {@link org.bejug.tictactoe.server.GameRegistry} which manages the available games.
     */
    private GameRegistry gameRegistry;


    /**
     * Constroctur, injecting the {@link org.bejug.tictactoe.server.GameRegistry} to be used.
     *
     * @param gameRegistry The {@link org.bejug.tictactoe.server.GameRegistry} to be used
     */
    @Inject
    @Named
    public TicTactToeEndpoint(final GameRegistry gameRegistry) {
        this.gameRegistry = gameRegistry;
    }

    /**
     * Called when a web-socket connection is opened. The connection is setup with a timeout. The session will be assigned to a player in a game.
     *
     * @param session the web-socket session
     * @throws IOException when sending web-socket message fails
     */
    @OnOpen
    public void onOpen(final Session session) throws IOException {
        LOGGER.info("Opening Endpoint for session " + session);
        session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);

        final Game lastGame = gameRegistry.getAvailableGame();
        session.getUserProperties().put(GAME_PROPERTY_KEY, lastGame);
        String sid = session.getId();

        Player player = new Player("Player " + sid, session);
        if (lastGame.getPlayer1() == null) {
            lastGame.setPlayer1(player);
            session.getBasicRemote().sendText(TicTactToeMessage.JOINED_AS_FIRST_PLAYER.getMessage());
            LOGGER.fine("First player joined " + player.getName() + ", message send back");
        } else {
            lastGame.setPlayer2(player);
            session.getBasicRemote().sendText(TicTactToeMessage.JOINED_AS_LAST_PLAYER.getMessage());
            LOGGER.fine("Second player joined " + player.getName() + ", message send back");
            lastGame.getPlayer1().getSession().getBasicRemote().sendText(TicTactToeMessage.SECOND_PLAYER_JOINED.getMessage());
            LOGGER.fine("Second player joined, first player notified");
        }
    }

    /**
     * Called when a web-socket connection is closed. This method ends the game played by the player with the matching session.
     * Also the opponent player's session is cleaned up. Lastly the game registry is notified of this game ending.
     *
     * @param session the web-socket session
     */
    @OnClose
// bug? public void onClose (Session session, CloseReason reason) {
    public void onClose(final Session session) {
        LOGGER.info("CLOSED! " + session);
        //LOGGER.info("reason: "+reason.getCloseCode()+ "-- "+reason.getReasonPhrase());
        Game game = (Game) session.getUserProperties().remove(GAME_PROPERTY_KEY);
        if (game != null) {
            Player opponent = game.getOpponentForSessionId(session.getId());
            if (opponent != null) {
                opponent.getSession().getUserProperties().remove(GAME_PROPERTY_KEY);
            }
            gameRegistry.gameHasFinished(game);
        }
    }

    /**
     * Called when a web-socket message arrives. This method will send the message through to the opponent player, if any.
     * @param message the message to be send
     * @param session the web-socket session
     * @throws IOException when sending web-socket message fails
     */
    @OnMessage
    public void onMessage(final String message, final Session session) throws IOException {
        LOGGER.info("Got message from session " + session + ": " + message);
        //TODO Check if move is valid! Always send message back also!

        Game game = (Game) session.getUserProperties().get(GAME_PROPERTY_KEY);
        Player opponent = game.getOpponentForSessionId(session.getId());
        if (opponent != null) {
            opponent.getSession().getBasicRemote().sendText(message);
        }

        //TODO Check if game ends

    }


}
