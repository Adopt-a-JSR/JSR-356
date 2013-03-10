package org.bejug.tictactoe.message;

import javax.websocket.Session;
import java.io.IOException;

/**
 * A TicTacToeMessageHandler which simply sends the given message to the given session with the given parameters.
 * @author mike
 */
public class SimpleTicTacToeMessageHandler extends AbstractTicTacToeMessageHandler {
    @Override
    public void handleMessage(final Session session, final TicTacToeMessage message, final String... parameters) throws UnhandledMessageException {
        try {
            sendMessageToPlayer(message, session, parameters);
        } catch (IOException e) {
            throw new UnhandledMessageException(message, "Sending the message failed, see cause", e);
        }
    }
}
