package org.bejug.tictactoe.server;

import javax.websocket.Session;

/**
 * @author mike
 */
public class Player {
    private String name;
    private Session session;

    public Player(String name, Session session) {
        this.name = name;
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
