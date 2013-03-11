package org.bejug.tictactoe.message;

/**
 * @author mike
 */
public class UnhandledMessageException extends Exception {
    private TicTacToeMessage message;

    /**
     * Constructor setting the {@link org.bejug.tictactoe.message.TicTacToeMessage} that could not be handled, with error message and root cause.
     *
     * @param message the TicTacToeMessage
     * @param errorMessage the error message
     * @param cause the root cause
     */
    public UnhandledMessageException(TicTacToeMessage message, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.message = message;
    }

    /**
     * Constructor setting the {@link org.bejug.tictactoe.message.TicTacToeMessage} that could not be handled, with error message.
     *
     * @param message the TicTacToeMessage
     * @param errorMessage the error message
     */
    public UnhandledMessageException(TicTacToeMessage message, String errorMessage) {
        super(errorMessage);
        this.message = message;
    }

}
