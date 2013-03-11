package org.bejug.tictactoe.message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * A message handler registry which stores the handlers in memory.
 *
 * @author mike
 */
@Named
@ApplicationScoped
public class InMemoryMessageHandlerRegistry implements MessageHandlerRegistry {
    /**
     * The map holding message handlers.
     */
    private Map<TicTacToeMessage, TicTacToeMessageHandler> registry;

    /**
     * Constructor, initializing handler for {@link org.bejug.tictactoe.message.TicTacToeMessage}s.
     */
    public InMemoryMessageHandlerRegistry() {
        SimpleTicTacToeMessageHandler simpleTicTacToeMessageHandler = new SimpleTicTacToeMessageHandler();
        SimpleTwoWayTicTacToeMessageHandler simpleTwoWayTicTacToeMessageHandler = new SimpleTwoWayTicTacToeMessageHandler();

        registry = new HashMap<TicTacToeMessage, TicTacToeMessageHandler>();
        registry.put(TicTacToeMessage.JOINED_AS_FIRST_PLAYER, simpleTicTacToeMessageHandler);
        registry.put(TicTacToeMessage.SECOND_PLAYER_JOINED, simpleTicTacToeMessageHandler);
        registry.put(TicTacToeMessage.JOINED_AS_LAST_PLAYER, simpleTicTacToeMessageHandler);
        registry.put(TicTacToeMessage.GAME_HAS_WINNER, simpleTwoWayTicTacToeMessageHandler);
        registry.put(TicTacToeMessage.OPPONENT_LEFT, simpleTicTacToeMessageHandler);
        registry.put(TicTacToeMessage.PLAYER_MADE_MOVE, new MoveTicTacToeMessageHandler());
        registry.put(TicTacToeMessage.PLAYER_CHANGED_NAME, new NameChangingTicTacToeMessageHandler());
        registry.put(TicTacToeMessage.INVALID_MESSAGE, simpleTicTacToeMessageHandler);
        registry.put(TicTacToeMessage.INVALID_MOVE, simpleTicTacToeMessageHandler);
    }


    @Override
    public void handle(final Session session, final TicTacToeMessage message, final String... parameters) throws UnhandledMessageException {
		TicTacToeMessageHandler handler = registry.get(message);
        if (handler != null) {
            handler.handleMessage(session, message, parameters);
        } else {
            throw new UnhandledMessageException(message, "No handler found for " + message + ".");
        }
    }
}
