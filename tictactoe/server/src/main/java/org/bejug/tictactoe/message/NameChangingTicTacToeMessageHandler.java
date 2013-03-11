package org.bejug.tictactoe.message;

import org.bejug.tictactoe.Preconditions;
import org.bejug.tictactoe.server.Game;
import org.bejug.tictactoe.server.Player;
import org.bejug.tictactoe.server.TicTacToeEndpoint;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Handles a player's name change message. If a valid name is provided, the opponent player will get a copy of the message.
 *
 * @author mike
 */
public class NameChangingTicTacToeMessageHandler extends AbstractTicTacToeMessageHandler {
    @Override
    public void handleMessage(final Session session, final TicTacToeMessage message, final String... parameters) throws UnhandledMessageException {
        Preconditions.checkNotNull(parameters);
        Preconditions.checkArgument(parameters.length == 1);
        String newName = parameters[0];

        Game game = (Game) session.getUserProperties().get(TicTacToeEndpoint.GAME_PROPERTY_KEY);
        String sid = session.getId();
        Player player = game.getPlayerForSessionId(sid);
        player.setName(newName);
        Player opponent = game.getOpponentForSessionId(sid);
        try {
            sendMessageToPlayer(message, opponent.getSession(), parameters);
        } catch (IOException e) {
            throw new UnhandledMessageException(message, "Sending the message failed, see cause", e);
        }
    }
}
