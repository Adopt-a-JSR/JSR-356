package org.bejug.tictactoe.server;

/**
 * @author mike
 */
public enum TicTactToeMessage {
    /**
     * Send to the first player joining a game.
     */
    JOINED_AS_FIRST_PLAYER("p1"),
    /**
     * Send to the first player when the second player joins a game.
     */
    SECOND_PLAYER_JOINED("p2"),
    /**
     * Send to the second player joining a game.
     */
    JOINED_AS_LAST_PLAYER("p3"),
    /**
     * Received when a player plays an "O", and resend to the other player.
     */
    O_PLAYED("o"),
    /**
     * Received when a player plays an "X", and resend to the other player.
     */
    X_PLAYED("x");

    /**
     * The message string that will be send for this message type.
     */
    private String message;

    /**
     * Creates a message type
     * @param message the message string that will be send for this message type
     */
    private TicTactToeMessage(final String message) {
        this.message = message;
    }

    /**
     * Returns the message that will be send for this message type
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
