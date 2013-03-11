package org.bejug.tictactoe.message;

import org.bejug.tictactoe.Preconditions;
import org.bejug.tictactoe.server.Game;
import org.bejug.tictactoe.server.TicTacToeEndpoint;

import javax.websocket.Session;
import java.io.IOException;

/**
 * TicTacToeMessageHandler specific for handling a move message. It checks if the move is valid. If valid, the player's opponent is notified by sending a {@link org.bejug.tictactoe.message.TicTacToeMessage#OPPONENT_MADE_MOVE}. If it is invalid, the player is send a {@link org.bejug.tictactoe.message.TicTacToeMessage#INVALID_MOVE} message.
 *
 * @author mike
 */
public class MoveTicTacToeMessageHandler extends AbstractTicTacToeMessageHandler {
    @Override
    public void handleMessage(final Session session, final TicTacToeMessage message, final String... parameters) throws UnhandledMessageException {
        Preconditions.checkNotNull(parameters);
        Preconditions.checkArgument(parameters.length == 1);
        String coordinate = parameters[0];
        Integer position;
        try {
            position = Integer.parseInt(coordinate);
        } catch (NumberFormatException e) {
            position = null;
        }
        //CheckArgument instead of checkNotNull, because we want an IllegalArgumentException!
        Preconditions.checkArgument(position != null);
        Preconditions.checkArgument(position >= 0 && position < 9);

        Game game = (Game) session.getUserProperties().get(TicTacToeEndpoint.GAME_PROPERTY_KEY);
        int row = position / 3;
        int column = position % 3;
        String sid = session.getId();

        try {
            if (game.registerMove(row, column, game.getPlayerForSessionId(sid))) {
                sendMessageToPlayer(TicTacToeMessage.OPPONENT_MADE_MOVE, game.getOpponentForSessionId(sid).getSession(), parameters);
            } else {
                sendMessageToPlayer(TicTacToeMessage.INVALID_MOVE, session, parameters);
            }
        } catch (IOException e) {
            throw new UnhandledMessageException(message, "Sending the message failed, see cause", e);
        }
    }
}
