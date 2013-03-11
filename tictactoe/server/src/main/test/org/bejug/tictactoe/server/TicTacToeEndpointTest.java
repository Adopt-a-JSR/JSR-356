package org.bejug.tictactoe.server;

import org.bejug.tictactoe.message.MessageHandlerRegistry;
import org.bejug.tictactoe.message.TicTacToeMessage;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.net.URI;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author mike
 */
@RunWith(MockitoJUnitRunner.class)
public class TicTacToeEndpointTest {

    private TicTacToeEndpoint endpoint;

    @Mock
    private GameRegistry gameRegistry;
    @Mock
    private MessageHandlerRegistry messageHandlerRegistry;


    @Mock
    private Map<String, Object> userProperties;

    @Before
    public void setUp() {
        endpoint = new TicTacToeEndpoint(gameRegistry, messageHandlerRegistry);
    }

    @Test
    public void testOnOpenNewGame() throws Exception {
        Session session = Mockito.mock(Session.class);
        Game game = new Game();

        when(session.getRequestURI()).thenReturn(new URI("ws://py.server.host:1234/some/endpoint/Mike+Seghers"));
        when(gameRegistry.getAvailableGame()).thenReturn(game);

        endpoint.onOpen(session);

        verify(session).setMaxIdleTimeout(TicTacToeEndpoint.MAX_IDLE_TIMEOUT);
        verify(messageHandlerRegistry).handle(session, TicTacToeMessage.JOINED_AS_FIRST_PLAYER);

        assertThat(game.getPlayer1(), notNullValue());
        assertThat(game.getPlayer1().getName(), is(equalTo("Mike Seghers")));
    }

    @Test
    public void testOnOpenNewGameNoPlayerName() throws Exception {
        Session session = Mockito.mock(Session.class);
        Game game = new Game();

        when(session.getRequestURI()).thenReturn(new URI("ws://py.server.host:1234/some/endpoint"));
        when(session.getId()).thenReturn("123456");
        when(gameRegistry.getAvailableGame()).thenReturn(game);

        endpoint.onOpen(session);

        verify(session).setMaxIdleTimeout(TicTacToeEndpoint.MAX_IDLE_TIMEOUT);
        verify(messageHandlerRegistry).handle(session, TicTacToeMessage.JOINED_AS_FIRST_PLAYER);

        assertThat(game.getPlayer1(), notNullValue());
        assertThat(game.getPlayer1().getName(), is(equalTo("Player 123456")));
    }

    @Test
    public void testOnOpenExistingGame() throws Exception {
        Session player2Session = Mockito.mock(Session.class);
        Session player1Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("P1", player1Session));

        when(gameRegistry.getAvailableGame()).thenReturn(game);
        when(player2Session.getRequestURI()).thenReturn(new URI("ws://py.server.host:1234/some/endpoint/Johan+Vos"));

        endpoint.onOpen(player2Session);

        verify(player2Session).setMaxIdleTimeout(TicTacToeEndpoint.MAX_IDLE_TIMEOUT);
        verify(messageHandlerRegistry).handle(player2Session, TicTacToeMessage.JOINED_AS_LAST_PLAYER);
        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.SECOND_PLAYER_JOINED, "Johan Vos");

        assertThat(game.getPlayer2(), notNullValue());
        assertThat(game.getPlayer2().getName(), is(equalTo("Johan Vos")));
    }

    @Test
    public void testOnClosePlayer1Leaving() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("Stijn Van den Enden", player1Session));
        game.setPlayer2(new Player("Renato Cavalcanti", player2Session));

        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(player2Session.getId()).thenReturn("sid2");
        when(userProperties.remove(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onClose(player1Session);

        verify(userProperties, Mockito.times(2)).remove(TicTacToeEndpoint.GAME_PROPERTY_KEY);
        verify(gameRegistry).gameHasFinished(game);
        verify(messageHandlerRegistry).handle(player2Session, TicTacToeMessage.OPPONENT_LEFT);
        verify(player2Session).close();
    }

    @Test
    public void testOnClosePlayer2Leaving() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("Mike Seghers", player1Session));
        game.setPlayer2(new Player("Stijn Van den Enden", player2Session));

        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(player2Session.getId()).thenReturn("sid2");
        when(userProperties.remove(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onClose(player2Session);

        verify(userProperties, Mockito.times(2)).remove(TicTacToeEndpoint.GAME_PROPERTY_KEY);
        verify(gameRegistry).gameHasFinished(game);

        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.OPPONENT_LEFT);
        verify(player1Session).close();
    }

    @Test
    public void testOnCloseNoOpponent() throws Exception {
        Session player1Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("Mike Seghers", player1Session));

        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(userProperties.remove(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onClose(player1Session);

        verify(userProperties).remove(TicTacToeEndpoint.GAME_PROPERTY_KEY);
        verify(gameRegistry).gameHasFinished(game);

        verifyNoMoreInteractions(messageHandlerRegistry);
    }

    @Test
    public void testOnCloseSessionHasNoGame() throws Exception {
        Session player1Session = Mockito.mock(Session.class);

        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(userProperties.remove(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(null);

        endpoint.onClose(player1Session);

        verify(userProperties).remove(TicTacToeEndpoint.GAME_PROPERTY_KEY);
        verifyNoMoreInteractions(gameRegistry);
        verifyNoMoreInteractions(messageHandlerRegistry);
        verifyNoMoreInteractions(userProperties);
    }

    @Test
    public void testOnMessageMadeMove() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("sid1", player1Session));
        game.setPlayer2(new Player("sid2", player2Session));

        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(player2Session.getId()).thenReturn("sid2");
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onMessage("pm 0", player1Session);

        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "0");
        verifyNoMoreInteractions(messageHandlerRegistry, gameRegistry);
    }

    @Test
    public void testOnMessageMadeMovePadded() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);

        Game game = new Game();
        game.setPlayer1(new Player("sid1", player1Session));
        game.setPlayer2(new Player("sid2", player2Session));

        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(player2Session.getId()).thenReturn("sid2");
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        endpoint.onMessage("     pm      0     1      2  ", player1Session);

        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "0", "1", "2");
        verifyNoMoreInteractions(messageHandlerRegistry, gameRegistry);
    }

    @Test
    public void testOnMessageWithoutParamsGameHasWinner() throws Exception {
        Session player1Session = Mockito.mock(Session.class);
        Session player2Session = Mockito.mock(Session.class);
        Game game = spy(new Game());
        Player player1 = new Player("Johan Vos", player1Session);
        game.setPlayer1(player1);
        Player player2 = new Player("Stijn Van den Enden", player2Session);
        game.setPlayer2(player2);


        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getId()).thenReturn("sid1");
        when(player2Session.getId()).thenReturn("sid2");
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        when(game.checkForWinner()).thenReturn(player1);

        endpoint.onMessage("pc", player1Session);

        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.PLAYER_CHANGED_NAME);
        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.GAME_HAS_WINNER, "Johan Vos");

        verify(gameRegistry).gameHasFinished(game);
        verifyNoMoreInteractions(messageHandlerRegistry, gameRegistry);
    }

    @Test
    public void testOnMessageUnknown() throws Exception {
        Session player1Session = Mockito.mock(Session.class);

        endpoint.onMessage("UNKNOWN", player1Session);

        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.INVALID_MESSAGE);
        verifyNoMoreInteractions(messageHandlerRegistry, gameRegistry, userProperties);
    }

    @Test
    public void testOnMessageServerMessage() throws Exception {
        Session player1Session = Mockito.mock(Session.class);

        endpoint.onMessage("p1", player1Session);

        verify(messageHandlerRegistry).handle(player1Session, TicTacToeMessage.INVALID_MESSAGE);
        verifyNoMoreInteractions(messageHandlerRegistry, gameRegistry, userProperties);
    }
}
