package org.bejug.tictactoe.server;

import java.util.UUID;

/**
 * Represents a Tic-Tac-Toe game.
 *
 * @author johan
 * @author mike
 */
public class Game {

    /**
     * The game's unique identifier.
     */
    private String uid;

    /**
     * The game's board.
     */
    private int[][] board = new int[3][3];

    /**
     * The first player.
     */
    private Player player1;

    /**
     * The second player.
     */
    private Player player2;

    /**
     * Constructor. Generates the uid.
     */
    public Game() {
        this.uid = UUID.randomUUID().toString();
    }

    /**
     * Returns the first player.
     *
     * @return the first player
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Sets the first player.
     *
     * @param player1 the first player to set
     */
    public void setPlayer1(final Player player1) {
        this.player1 = player1;
    }

    /**
     * Returns the second player.
     *
     * @return the second player
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Sets the second player.
     *
     * @param player2 the second player to set
     */
    public void setPlayer2(final Player player2) {
        this.player2 = player2;
    }

    /**
     * Checks if a game is ready to be played. A game is ready when both players are in.
     *
     * @return true when two players are in the game, false otherwise
     */
    public boolean isReadyToPlay() {
        return player1 != null && player2 != null;
    }

    /**
     * If one of the players has a session id that matches the given sid, the other player playing in this game is returned.
     * If the given sid doesn't match a players sid, or when the opponent player is not in yet, null is returned.
     *
     * @param sid The session id that might matches the session id of a player
     * @return The opponent of the player which session id matches the given sid, or null
     */
    public Player getOpponentForSessionId(final String sid) {

        Player result = null;
        if (player1 != null && player1.getSession().getId().equals(sid)) {
            result = player2;
        } else if (player2 != null && player2.getSession().getId().equals(sid)) {
            result = player1;
        }
        return result;
    }
}
