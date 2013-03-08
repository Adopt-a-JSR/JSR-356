package org.bejug.tictactoe.server;

import java.util.LinkedList;
import java.util.List;

/**
 * GameRegistry implementation which keeps games in memory.
 * @author mike
 */
public final class InMemoryGameRegistry implements GameRegistry {
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
    private InMemoryGameRegistry() {
        games = new LinkedList<Game>();
    }

    /**
     * Get the singleton instance.
     * @return the singleton instance
     */
    public static synchronized InMemoryGameRegistry getInstance() {
        if (instance == null) {
            instance = new InMemoryGameRegistry();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Game getAvailableGame() {
        Game last = games.get(games.size() - 1);
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
    public void gameHasFinished(final Game game) {
        synchronized (games) {
            games.remove(game);
        }
    }
}
