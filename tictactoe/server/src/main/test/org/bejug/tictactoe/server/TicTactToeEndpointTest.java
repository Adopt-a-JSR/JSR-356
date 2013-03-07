package org.bejug.tictactoe.server;

import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 * @author mike
 */
public class TicTactToeEndpointTest {

    private TicTactToeEndpoint endpoint;

    @Test
    public void testOnOpenNewGame() throws Exception {
        Session session = Mockito.mock(Session.class);
        RemoteEndpoint.Basic basic = Mockito.mock(RemoteEndpoint.Basic.class);
        Mockito.when(session.getBasicRemote()).thenReturn(basic);

        TicTactToeEndpoint.setLastGame(new Game());

        endpoint = new TicTactToeEndpoint();
        endpoint.onOpen(session);

        Mockito.verify(basic).sendText("p1");
    }

    /*@Test
    public void testOnOpenExistingGame() throws Exception {
        Session session = Mockito.mock(Session.class);
        RemoteEndpoint.Basic basic = Mockito.mock(RemoteEndpoint.Basic.class)
        Mockito.when(session.getBasicRemote()).thenReturn(basic);

        Game game = new Game();
        game.setPlayer1("Player 1");
        TicTactToeEndpoint.setLastGame(game);

        endpoint = new TicTactToeEndpoint();
        endpoint.onOpen(session);

        Mockito.verify(basic).sendText("p3");
    }

    @Test
    public void testOnClose() throws Exception {

    }

    @Test
    public void testOnMessage() throws Exception {

    }*/
}
