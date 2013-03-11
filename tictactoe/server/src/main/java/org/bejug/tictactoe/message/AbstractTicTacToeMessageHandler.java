package org.bejug.tictactoe.message;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;

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
        message.setExtra(Arrays.asList(extraParameters));
		RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
		try {
			
			basicRemote.sendObject(message);
			// this is moved to the MessageEncoder
	//        String fullText;
	//        if (extraParameters != null && extraParameters.length > 0) {
	//            StringBuilder builder = new StringBuilder(message.getMessage());
	//            for (String extraParameter : extraParameters) {
	//                builder.append(" ").append(extraParameter.trim());
	//            }
	//            fullText = builder.toString();
	//        } else {
	//            fullText = message.getMessage();
	//        basicRemote.sendText(fullText);
	//        basicRemote.sendText(fullText);
		} catch (EncodeException ex) {
			Logger.getLogger(AbstractTicTacToeMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
    }
}
