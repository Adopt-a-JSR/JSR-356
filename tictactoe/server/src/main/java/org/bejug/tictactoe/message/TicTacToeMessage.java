package org.bejug.tictactoe.message;

import java.util.List;

/**
 * Tic-Tac-Toe web-socket messages which are supported.
 *
 * @author mike
 */
public enum TicTacToeMessage {
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
     * Send when a game is won by a player.
     */
    GAME_HAS_WINNER("p4"),
    /**
     * Send to a player when the other player leaves the game.
     */
    OPPONENT_LEFT("p5"),
    /**
     * Received when a player makes a move.
     */
    PLAYER_MADE_MOVE("pm", true),
    /**
     * Received when a player changes her/his name, and resend to the opponent player.
     */
    PLAYER_CHANGED_NAME("pc", true),
    /**
     * Send to a player when the other player made a valid move (PLAYER_MADE_MOVE).
     */
    OPPONENT_MADE_MOVE("om"),
    /**
     * Send back to a player when an invalid message is received.
     */
    INVALID_MESSAGE("e1"),
    /**
     * Send back to a player when an invalid move has been received.
     */
    INVALID_MOVE("e2");

    /**
     * The message string that will be send for this message type.
     */
    private String message;
	
	/**
	 * The additional extra parameters supplied with this message
	 */
	private List<String> extra;

    /**
     * Flag indicating whether this message can be send by a client.
     */
    private boolean clientMessage;

    /**
     * Constructor.
     *
     * @param message the message string that will be send for this message type
     */
    private TicTacToeMessage(final String message) {
        this.message = message;
    }

    /**
     * Constructor specifying the actual text message and the client flag, indicating whether or not the client can send this type of message.
     *
     * @param message       the message
     * @param clientMessage flag indicating whether this message can be send by a client
     */
    private TicTacToeMessage(final String message, final boolean clientMessage) {
        this(message);
        this.clientMessage = clientMessage;
    }

    /**
     * Returns the message that will be send for this message type.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }
	
	/**
	 * Returns the (optional) additional message-specific parameters 
	 * @return the additional parameters
	 */
	public List<String> getExtra() {
		return extra;
	}
	
	public void setExtra (List<String> params) {
		this.extra = params;
	}

    /**
     * Return the flag indicating whether this message can be send by a client.
     * @return true if sendable by a client, false otherwise
     */
    public boolean isClientMessage() {
        return clientMessage;
    }

    /**
     * Returns the TicTacToeMessage which message attribute matches the given message parameter. If no match is found, null is returned.
     *
     * @param message The message to be matched with the TicTacToeMessage message attribute
     * @return the TicTacToeMessage which message attribute matches the given message parameter, or null
     */
    public static TicTacToeMessage ticTacToeMessage(final String message, List<String> params) {
        TicTacToeMessage result = null;
        for (TicTacToeMessage ticTacToeMessage : values()) {
            if (ticTacToeMessage.getMessage().equals(message)) {
                result = ticTacToeMessage;
				result.extra = params;
                break;
            }
        }
        return result;
    }
}
