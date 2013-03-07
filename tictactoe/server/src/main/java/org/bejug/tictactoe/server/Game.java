package org.bejug.tictactoe.server;

import java.util.UUID;

/**
 *
 * @author johan
 */
public class Game {

	private String uid;
	
	private int[][] board = new int[3][3];
	private Player player1;
	private Player player2;

	public Game () {
		this.uid = UUID.randomUUID().toString();
	}
	
	/**
	 * @return the player1
	 */
	public Player getPlayer1() {
		return player1;
	}

	/**
	 * @param player1 the player1 to set
	 */
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	/**
	 * @return the player2
	 */
	public Player getPlayer2() {
		return player2;
	}

	/**
	 * @param player2 the player2 to set
	 */
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

    public boolean isReadyToPlay() {
        return player1 != null && player2 != null;
    }

    Player getPlayerForSessionId(String sid) {
        Player result = null;
        if (player1.getSession().getId().equals(sid)) {
            result = player1;
        } else if (player2.getSession().getId().equals(sid)) {
            result = player2;
        }
        return result;
    }
	
	
}
