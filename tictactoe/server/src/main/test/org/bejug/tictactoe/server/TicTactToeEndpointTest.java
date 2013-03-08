package org.bejug.tictactoe.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.Map;

/**
 * @author mike
 */
@RunWith(MockitoJUnitRunner.class)
public class TicTactToeEndpointTest {

    private TicTactToeEndpoint endpoint;

    @Mock
    private GameRegistry gameRegistry;

    @Mock
    private Map<String, Object> userProperties;

    @Before
    public void setUp() {
        endpoint = new TicTactToeEndpoint();
        GameRegistryFactory.setOverride(gameRegistry);
    }

    @Test
    public void testOnOpenNewGame() throws Exception {
        Session session = Mockito.mock(Session.class);
        RemoteEndpoint.Basic basic = Mockito.mock(RemoteEndpoint.Basic.class);
        Mockito.when(session.getBasicRemote()).thenReturn(basic);

        Mockito.when(gameRegistry.getAvailableGame()).thenReturn(new Game());

        endpoint.onOpen(session);

        Mockito.verify(session).setMaxIdleTimeout(TicTactToeEndpoint.MAX_IDLE_TIMEOUT);
        Mockito.verify(basic).sendText("p1");
    }

    @Test
    public void testOnOpenExistingGame() throws Exception {
        Session session = Mockito.mock(Session.class);
        Session player1Session = Mockito.mock(Session.class);
        RemoteEndpoint.Basic basic = Mockito.mock(RemoteEndpoint.Basic.class);
        Mockito.when(session.getBasicRemote()).thenReturn(basic);
        RemoteEndpoint.Basic player1Basic = Mockito.mock(RemoteEndpoint.Basic.class);
        Mockito.when(player1Session.getBasicRemote()).thenReturn(player1Basic);

        Game game = new Game();
        game.setPlayer1(new Player("sid", player1Session));
        Mockito.when(gameRegistry.getAvailableGame()).thenReturn(game);

        endpoint.onOpen(session);

        Mockito.verify(session).setMaxIdleTimeout(TicTactToeEndpoint.MAX_IDLE_TIMEOUT);
        Mockito.verify(basic).sendText("p3");
        Mockito.verify(player1Basic).sendText("p2");
    }

    @Test
    public void testOnClosePlayer1Leaving() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("sid1", player1Session));
        game.setPlayer2(new Player("sid2", player2Session));

        Mockito.when(player1Session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(player2Session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(player1Session.getId()).thenReturn("sid1");
        Mockito.when(player2Session.getId()).thenReturn("sid2");
        Mockito.when(userProperties.remove(TicTactToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onClose(player1Session);

        Mockito.verify(userProperties, Mockito.times(2)).remove(TicTactToeEndpoint.GAME_PROPERTY_KEY);
        Mockito.verify(gameRegistry).gameHasFinished(game);
    }

    @Test
    public void testOnClosePlayer2Leaving() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("sid1", player1Session));
        game.setPlayer2(new Player("sid2", player2Session));

        Mockito.when(player1Session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(player2Session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(player1Session.getId()).thenReturn("sid1");
        Mockito.when(player2Session.getId()).thenReturn("sid2");
        Mockito.when(userProperties.remove(TicTactToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onClose(player2Session);

        Mockito.verify(userProperties, Mockito.times(2)).remove(TicTactToeEndpoint.GAME_PROPERTY_KEY);
        Mockito.verify(gameRegistry).gameHasFinished(game);
    }

    @Test
    public void testOnMessage() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);
        RemoteEndpoint.Basic basic = Mockito.mock(RemoteEndpoint.Basic.class);

        Game game = new Game();
        game.setPlayer1(new Player("sid1", player1Session));
        game.setPlayer2(new Player("sid2", player2Session));

        Mockito.when(player1Session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(player1Session.getId()).thenReturn("sid1");
        Mockito.when(player2Session.getId()).thenReturn("sid2");
        Mockito.when(userProperties.get(TicTactToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        Mockito.when(player2Session.getBasicRemote()).thenReturn(basic);

        endpoint.onMessage("Some message", player1Session);

        Mockito.verify(basic).sendText("Some message");
    }
    @Test
    public void testOnMessageNoOpponent() throws Exception {
        Session player1Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("sid1", player1Session));

        Mockito.when(player1Session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(player1Session.getId()).thenReturn("sid1");
        Mockito.when(userProperties.get(TicTactToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onMessage("Some message", player1Session);
    }


}
