package org.bejug.tictactoe.message;

import org.bejug.tictactoe.server.Game;
import org.bejug.tictactoe.server.TicTacToeEndpoint;

import javax.websocket.Session;
import java.io.IOException;

/**
 * A TicTacToeMessageHandler which simply sends the given message to the given session and the opponent's session, based on the user property stored in the given session.
 *
 * @author mike
 */
public class SimpleTwoWayTicTacToeMessageHandler extends AbstractTicTacToeMessageHandler {
    @Override
    public void handleMessage(final Session session, final TicTacToeMessage message, final String... parameters) throws UnhandledMessageException {
        try {
            Game game = (Game) session.getUserProperties().get(TicTacToeEndpoint.GAME_PROPERTY_KEY);
            sendMessageToPlayer(message, game.getPlayer1().getSession(), parameters);
            sendMessageToPlayer(message, game.getPlayer2().getSession(), parameters);
        } catch (IOException e) {
            throw new UnhandledMessageException(message, "Sending the message failed, see cause", e);
        }
    }
}
