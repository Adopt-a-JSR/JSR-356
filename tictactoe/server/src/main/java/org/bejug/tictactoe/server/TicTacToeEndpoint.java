package org.bejug.tictactoe.server;

import org.apache.commons.lang3.Validate;
import org.bejug.tictactoe.Preconditions;
import org.bejug.tictactoe.Splitter;
import org.bejug.tictactoe.message.MessageHandlerRegistry;
import org.bejug.tictactoe.message.TicTacToeMessage;
import org.bejug.tictactoe.message.UnhandledMessageException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.bejug.tictactoe.message.MessageEncoder;

/**
 * A web-socket server endpoint for Tic-Tac-Toe games. This endpoint manages games through a
 * {@link org.bejug.tictactoe.server.GameRegistry}.
 *
 * @author johan
 * @author mike
 */
@ServerEndpoint(value="/endpoint", encoders = MessageEncoder.class)
public class TicTacToeEndpoint {
    /**
     * Name for storing a game in the session's user properties.
     */
    public static final String GAME_PROPERTY_KEY = "Game";

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
     * The {@link org.bejug.tictactoe.message.MessageHandlerRegistry} which holds the various handlers for the various messages that have been received.
     */
    private MessageHandlerRegistry handlerRegistry;


    /**
     * Constroctur, injecting the GameRegistry and MessageHandlerRegistry to be used.
     *
     * @param gameRegistry    The to GameRegistry be used
     * @param handlerRegistry the MessageHandlerRegistry to be used
     */
    @Inject
    @Named
    public TicTacToeEndpoint(final GameRegistry gameRegistry, final MessageHandlerRegistry handlerRegistry) {
        this.gameRegistry = gameRegistry;
        this.handlerRegistry = handlerRegistry;
    }

    /**
     * Called when a web-socket connection is opened. The connection is setup with a timeout. The session will be assigned to a player in a game.
     *
     * @param session the web-socket session
     * @throws UnhandledMessageException when handling messages fails
     */
    @OnOpen
    public void onOpen(final Session session) throws UnhandledMessageException {
        LOGGER.info("Opening Endpoint for session " + session);
        session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);

        final Game lastGame = gameRegistry.getAvailableGame();
        session.getUserProperties().put(GAME_PROPERTY_KEY, lastGame);
        String sid = session.getId();

        String uri = session.getRequestURI().toString();
        String name;
        try {
            name = URLDecoder.decode(uri.substring(uri.lastIndexOf('/') + 1), "UTF-8");
            if ("endpoint".equals(name)) {
                name = "Player " + session.getId();
            }
        } catch (UnsupportedEncodingException e) {
            name = "Player " + session.getId();
        }

        Player player = new Player(name, session);
        if (lastGame.getPlayer1() == null) {
            lastGame.setPlayer1(player);
            handlerRegistry.handle(session, TicTacToeMessage.JOINED_AS_FIRST_PLAYER);
            LOGGER.fine("First player joined " + player.getName() + ", message send back");
        } else {
            lastGame.setPlayer2(player);
            handlerRegistry.handle(session, TicTacToeMessage.JOINED_AS_LAST_PLAYER);
            LOGGER.fine("Second player joined " + player.getName() + ", message send back");
            handlerRegistry.handle(lastGame.getPlayer1().getSession(), TicTacToeMessage.SECOND_PLAYER_JOINED, player.getName());
            LOGGER.fine("Second player joined, first player notified");
        }
    }

    /**
     * Called when a web-socket connection is closed. This method ends the game played by the player with the matching session.
     * Also the opponent player's session is cleaned up. Lastly the game registry is notified of this game ending.
     *
     * @param session the web-socket session
     * @throws UnhandledMessageException when message handling fails
     * @throws IOException               when closing other sockets fails
     */
    @OnClose
// bug? public void onClose (Session session, CloseReason reason) {
    public void onClose(final Session session) throws UnhandledMessageException, IOException {
        LOGGER.info("CLOSED! " + session);
        //LOGGER.info("reason: "+reason.getCloseCode()+ "-- "+reason.getReasonPhrase());
        Game game = (Game) session.getUserProperties().remove(GAME_PROPERTY_KEY);
        if (game != null) {
            Player opponent = game.getOpponentForSessionId(session.getId());
            if (opponent != null) {
                Session opponentSession = opponent.getSession();
                opponentSession.getUserProperties().remove(GAME_PROPERTY_KEY);
                handlerRegistry.handle(opponentSession, TicTacToeMessage.OPPONENT_LEFT);
                endPlayerSession(opponent);
            }
            gameRegistry.gameHasFinished(game);
        }
    }

    /**
     * Called when a web-socket message arrives. This method will hanle valid incoming messages through the MessageHandlerRegistry. If the message is invalid an exception will be thrown. Invalid messages are messages that are not recognized by the system, messages with bad parameters or messages that shouldn't have been send by a client in the first place.
     *
     * @param message the message to be send
     * @param session the web-socket session
     * @throws UnhandledMessageException when message handling fails
     * @throws IOException               when sending web-socket message fails
     */
    @OnMessage
    public void onMessage(final String message, final Session session) throws UnhandledMessageException, IOException {
        LOGGER.info("Got message from session " + session + ": " + message);
        Preconditions.checkNotNull(message);

        Validate.notNull(message);

        Iterator<String> messageWitParams = Splitter.on(' ').omitEmptyStrings().trimResults().split(message).iterator();
        List<String> params = new ArrayList<String>();
        String effectiveMessage = messageWitParams.next();
        while (messageWitParams.hasNext()) {
            params.add(messageWitParams.next());
        }
        TicTacToeMessage tttMessage = TicTacToeMessage.ticTacToeMessage(effectiveMessage, params);
        if (tttMessage == null || !tttMessage.isClientMessage()) {
            handlerRegistry.handle(session, TicTacToeMessage.INVALID_MESSAGE);
        } else {
            if (params == null || params.isEmpty()) {
                handlerRegistry.handle(session, tttMessage);
            } else {
                handlerRegistry.handle(session, tttMessage, params.toArray(new String[params.size()]));
            }

            Game game = (Game) session.getUserProperties().get(GAME_PROPERTY_KEY);
            Player winner = game.checkForWinner();
            if (winner != null) {
                //Game ends
                handlerRegistry.handle(session, TicTacToeMessage.GAME_HAS_WINNER, winner.getName());
                endPlayerSession(game.getPlayer1());
                endPlayerSession(game.getPlayer2());
                gameRegistry.gameHasFinished(game);
            }
        }


    }

    /**
     * End a player's session by closing it. This is triggered when game end is detected or if an opponent player leaves the game.
     *
     * @param player The player
     * @throws IOException when closing the web-socket fails
     */
    private void endPlayerSession(final Player player) throws IOException {
        if (player != null) {
            Session session = player.getSession();
            if (session != null) {
                session.close();
            }
        }
    }


}
