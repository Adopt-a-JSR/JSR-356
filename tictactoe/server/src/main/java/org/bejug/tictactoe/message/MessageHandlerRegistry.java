package org.bejug.tictactoe.message;

import javax.websocket.Session;

/**
 * A message handler registry keeps track of handlers for a given message.
 *
 * @author mike
 */
public interface MessageHandlerRegistry {
    /**
     * Handle the give message by using a registered {@link org.bejug.tictactoe.message.TicTacToeMessageHandler}. If no such handler can be found an UnhandledMessageException is thrown.
     *
     * @param session    the main initiator of the message
     * @param message    the message the message to handle
     * @param parameters optional message parameters
     * @throws UnhandledMessageException when an appropriate handler can not be found, or if the handler itself throws this exception.
     */
    void handle(Session session, TicTacToeMessage message, String... parameters) throws UnhandledMessageException;
}
