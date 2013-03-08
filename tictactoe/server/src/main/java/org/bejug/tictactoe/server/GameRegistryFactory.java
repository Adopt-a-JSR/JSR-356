package org.bejug.tictactoe.server;

/**
 * @author mike
 */
public class GameRegistryFactory {
    /**
     * Singleton instance.
     */
    private static GameRegistryFactory instance;

    /**
     * GameRegistry override.
     */
    private static GameRegistry override;

    /**
     * Default constructor, setting a default GameRegistry implementation.
     */
    private GameRegistryFactory() {
    }

    /**
     * Get the singleton instance.
     *
     * @return the singleton instance
     */
    public static synchronized GameRegistryFactory getInstance() {
        if (instance == null) {
            instance = new GameRegistryFactory();
        }
        return instance;
    }

    /**
     * Sets the non-default GameRegistry implementation. Package private, since mostly meant to be used in unit tests.
     *
     * @param gameRegistry the non-default GameRegistry
     */
    static void setOverride(final GameRegistry gameRegistry) {
        override = gameRegistry;
    }

    /**
     * Returns the GameRegistry
     * @return the GameRegistry
     */
    public GameRegistry getGameRegistry() {
        return (override == null ? InMemoryGameRegistry.getInstance() : override);
    }
}
