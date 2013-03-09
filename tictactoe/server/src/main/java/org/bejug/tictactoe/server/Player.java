package org.bejug.tictactoe.server;

import javax.websocket.Session;

/**
 * Represents a Tic-Tac-Toe player.
 *
 * @author mike
 */
public class Player {
    /**
     * The player's name.
     */
    private String name;

    /**
     * The player's web-socket session.
     */
    private Session session;

    /**
     * Constructor.
     *
     * @param name    The player's name
     * @param session Tje player's web-socket session
     */
    public Player(final String name, final Session session) {
        this.name = name;
        this.session = session;
    }

    /**
     * Returns the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player's name.
     *
     * @param name the player's name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the player's session.
     *
     * @return the player's session
     */
    public Session getSession() {
        return session;
    }
}
