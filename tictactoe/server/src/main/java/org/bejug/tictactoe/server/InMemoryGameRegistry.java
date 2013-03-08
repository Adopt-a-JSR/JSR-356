package org.bejug.tictactoe.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.LinkedList;
import java.util.List;

/**
 * GameRegistry implementation which keeps games in memory.
 *
 * @author mike
 */
@Named
@ApplicationScoped
public class InMemoryGameRegistry implements GameRegistry {
    /**
     * Singleton instance.
     */
    private static InMemoryGameRegistry instance;

    /**
     * The list of games.
     */
    private final List<Game> games;

    /**
     * Constructor.
     */
    public InMemoryGameRegistry() {
        games = new LinkedList<Game>();

    }

    /**
     * Get the last game in the games list, or a new one if the list is empty.
     * @return The last game in the list, or a new one if the list was empty.
     */
    private Game getLastInListOrNew() {
        Game result;
        if (games.size() == 0) {
            result = new Game();
            games.add(result);
        } else {
            result = games.get(games.size() - 1);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Game getAvailableGame() {
        Game last = getLastInListOrNew();
        if (last.isReadyToPlay()) {
            last = new Game();
            games.add(last);
        }
        return last;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void gameHasFinished(final Game game) {
        games.remove(game);
    }
}
