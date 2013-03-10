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
     * Player symbol enumeration, representing the symbol a player should play.
     */
    public enum Symbol {
        /**
         * The first player's symbol. This is what should be visualized when the first player makes a move.
         */
        PLAYER_ONE_SYMBOL("O"),
        /**
         * The second player's symbol. This is what should be visualized when the second player makes a move.
         */
        PLAYER_TWO_SYMBOL("X");

        /**
         * The symbol.
         */
        private String symbol;

        /**
         * Constructor.
         *
         * @param symbol the symbol
         */
        private Symbol(final String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    /**
     * The game's unique identifier.
     */
    private String uid;

    /**
     * The game's board.
     */
    private Symbol[][] board = new Symbol[3][3];

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

    /**
     * Returns the player which has a session id that matches the given sid.
     * If the given sid doesn't match a player's sid, null is returned.
     *
     * @param sid The session id that might matches the session id of a player
     * @return The player which session id matches the given sid, or null
     */
    public Player getPlayerForSessionId(final String sid) {

        Player result = null;
        if (player1 != null && player1.getSession().getId().equals(sid)) {
            result = player1;
        } else if (player2 != null && player2.getSession().getId().equals(sid)) {
            result = player2;
        }
        return result;
    }

    /**
     * Register a move at the given row and column for the given player.
     *
     * @param row    the row at which to make the move
     * @param column the row at which to make the move
     * @param player the player making the move, the game will decide which symbol should be put at this position.
     * @return true if the move was valid, false otherwise (this occurs when the position is already taken, or when the given player does not belong in the game.
     */
    public boolean registerMove(final int row, final int column, final Player player) {
        boolean result = false;
        if (board[row][column] == null) {
            if (player == player1) {
                board[row][column] = Symbol.PLAYER_ONE_SYMBOL;
                result = true;
            } else if (player == player2) {
                board[row][column] = Symbol.PLAYER_TWO_SYMBOL;
                result = true;
            }
        }
        return result;
    }

    /**
     * Check if the board yields a winner.
     *
     * @return The winning player, or null if the game has no winner yet.
     */
    public Player checkForWinner() {
        //Check rows
        Player result = null;
        for (int i = 0; i < 3 && result == null; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                result = getPlayerForSymbol(board[i][0]);
            }
        }

        if (result == null) {
            //Check columns
            for (int i = 0; i < 3 && result == null; i++) {
                if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                    result = getPlayerForSymbol(board[0][i]);
                }
            }
        }

        if (result == null) {
            if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
                result = getPlayerForSymbol(board[0][0]);
            }
        }

        return result;
    }

    /**
     * Get the player for a given symbol.
     *
     * @param symbol the symbol
     * @return the player for the given symbol, or null.
     */
    private Player getPlayerForSymbol(final Symbol symbol) {
        Player result = null;
        if (symbol == Symbol.PLAYER_ONE_SYMBOL) {
            result = player1;
        } else if (symbol == Symbol.PLAYER_TWO_SYMBOL) {
            result = player2;
        }
        return result;
    }


}
