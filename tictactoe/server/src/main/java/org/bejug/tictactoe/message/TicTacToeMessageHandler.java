package org.bejug.tictactoe.message;

import javax.websocket.Session;

/**
 * @author mike
 */
public interface TicTacToeMessageHandler {
    void handleMessage(Session session, TicTacToeMessage message, String... parameters) throws UnhandledMessageException;
}
