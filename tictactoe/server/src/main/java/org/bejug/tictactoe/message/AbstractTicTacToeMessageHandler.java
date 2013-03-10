package org.bejug.tictactoe.message;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

/**
 * Abstract TicTacToeMessageHandler, defining a convenience method for sending text messages.
 *
 * @author mike
 */
public abstract class AbstractTicTacToeMessageHandler implements TicTacToeMessageHandler {
    /**
     * Helper method to send a message to a player. The optional extra parameters will be appended to the message, using spaces as separator.
     *
     * @param message         The message to be send
     * @param session         The web-socket session over which to send the message
     * @param extraParameters Optional extra parameters to send with the message
     * @throws java.io.IOException when sending the web-socket message fails
     */
    protected void sendMessageToPlayer(final TicTacToeMessage message, final Session session, final String... extraParameters) throws IOException {
        RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
        String fullText;
        if (extraParameters != null && extraParameters.length > 0) {
            StringBuilder builder = new StringBuilder(message.getMessage());
            for (String extraParameter : extraParameters) {
                builder.append(" ").append(extraParameter.trim());
            }
            fullText = builder.toString();
        } else {
            fullText = message.getMessage();
        }
        basicRemote.sendText(fullText);
    }
}
