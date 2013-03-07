package org.bejug.tictactoe.server;

/**
 * Game factory class.
 *
 * @author mike
 */
public interface GameRegistry {
    /**
     * Get an available game. An available game is a game that does not have 2 players yet. If no such game exists, a new one will be created.
     *
     * @return An available game.
     */
    Game getAvailableGame();

    /**
     * When a game is finished, this method should be called, so the register can be cleaned up.
     *
     * @param game The game that has been finished.
     */
    void gameHasFinished(Game game);
}
