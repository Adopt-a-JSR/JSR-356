package org.bejug.tictactoe.message;

import org.bejug.tictactoe.server.Game;
import org.bejug.tictactoe.server.Player;
import org.bejug.tictactoe.server.TicTacToeEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author mike
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleTwoWayTicTacToeMessageHandlerTest {
    private SimpleTwoWayTicTacToeMessageHandler handler;

    @Mock
    private Session player1Session;

    @Mock
    RemoteEndpoint.Basic player1Basic;

    @Mock
    private Session player2Session;

    @Mock
    RemoteEndpoint.Basic player2Basic;

    @Mock
    private Map<String, Object> userProperties;

    private Game game;

    @Before
    public void setUp() throws Exception {
        handler = new SimpleTwoWayTicTacToeMessageHandler();
        game = new Game();
        game.setPlayer1(new Player("Player 1", player1Session));
        game.setPlayer2(new Player("Player 2", player2Session));
    }

    @Test
    public void testHandleMessageNoParams() throws Exception {
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        handler.handleMessage(player1Session, TicTacToeMessage.GAME_HAS_WINNER);

        verify(player1Basic).sendText("p4");
        verify(player2Basic).sendText("p4");
    }

    @Test
    public void testHandleMessageNullParams() throws Exception {
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        handler.handleMessage(player1Session, TicTacToeMessage.GAME_HAS_WINNER, null);

        verify(player1Basic).sendText("p4");
        verify(player2Basic).sendText("p4");
    }

    @Test
    public void testHandleMessageWithParams() throws Exception {
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);

        handler.handleMessage(player1Session, TicTacToeMessage.GAME_HAS_WINNER, "1", "2", "3");

        verify(player1Basic).sendText("p4 1 2 3");
        verify(player2Basic).sendText("p4 1 2 3");
    }

    @Test(expected = UnhandledMessageException.class)
    public void testHandleMessageSendFailure() throws Exception {
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        doThrow(new IOException()).when(player2Basic).sendText("p4");

        try {
            handler.handleMessage(player1Session, TicTacToeMessage.GAME_HAS_WINNER);
        } catch (UnhandledMessageException e) {
            verify(player1Basic).sendText("p4");
            verify(player2Basic).sendText("p4");

            assertThat(e.getCause(), is(instanceOf(IOException.class)));
            throw e;
        }
    }
}
