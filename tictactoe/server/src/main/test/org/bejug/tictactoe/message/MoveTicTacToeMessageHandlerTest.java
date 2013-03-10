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
public class MoveTicTacToeMessageHandlerTest {

    private MoveTicTacToeMessageHandler handler;

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

    @Before
    public void setUp() {
        handler = new MoveTicTacToeMessageHandler();
    }

    @Test
    public void testHandleMessageFirstPlayer() throws Exception {
        Game game = spy(new Game());
        Player player1 = new Player("Johan Vos", player1Session);
        game.setPlayer1(player1);
        Player player2 = new Player("Mike Seghers", player2Session);
        game.setPlayer2(player2);

        when(player1Session.getId()).thenReturn("sid1");
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player2Session.getId()).thenReturn("sid2");
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        when(game.registerMove(0, 0, player1)).thenReturn(true);

        handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "0");

        verify(game).registerMove(0, 0, player1);
        verify(player2Basic).sendText(TicTacToeMessage.OPPONENT_MADE_MOVE.getMessage() + " 0");
        verifyNoMoreInteractions(player1Basic, player2Basic);
    }

    @Test
    public void testHandleMessageSecondPlayer() throws Exception {
        Game game = spy(new Game());
        Player player1 = new Player("Johan Vos", player1Session);
        game.setPlayer1(player1);
        Player player2 = new Player("Mike Seghers", player2Session);
        game.setPlayer2(player2);

        when(player1Session.getId()).thenReturn("sid1");
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player2Session.getId()).thenReturn("sid2");
        when(player2Session.getUserProperties()).thenReturn(userProperties);
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        when(game.registerMove(2, 2, player2)).thenReturn(true);

        handler.handleMessage(player2Session, TicTacToeMessage.PLAYER_MADE_MOVE, "8");

        verify(game).registerMove(2, 2, player2);
        verify(player1Basic).sendText(TicTacToeMessage.OPPONENT_MADE_MOVE.getMessage() + " 8");
        verifyNoMoreInteractions(player1Basic, player2Basic);
    }

    @Test(expected = NullPointerException.class)
    public void testHandleMessageNullExtraParam() throws Exception {
        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, null);
        } catch (NullPointerException e) {
            verifyNoMoreInteractions(player1Session, player2Session, player1Basic, player2Basic);
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleMessageNoExtraParam() throws Exception {
        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE);
        } catch (IllegalArgumentException e) {
            verifyNoMoreInteractions(player1Session, player2Session, player1Basic, player2Basic);
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleMessageNotANumberParam() throws Exception {
        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "NaN");
        } catch (IllegalArgumentException e) {
            verifyNoMoreInteractions(player1Session, player2Session, player1Basic, player2Basic);
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleMessageNumberToSmallParam() throws Exception {
        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "-1");
        } catch (IllegalArgumentException e) {
            verifyNoMoreInteractions(player1Session, player2Session, player1Basic, player2Basic);
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleMessageNumberToBigParam() throws Exception {
        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "9");
        } catch (IllegalArgumentException e) {
            verifyNoMoreInteractions(player1Session, player2Session, player1Basic, player2Basic);
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleMessageWrongNumberParam() throws Exception {
        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "-1");
        } catch (IllegalArgumentException e) {
            verifyNoMoreInteractions(player1Session, player2Session, player1Basic, player2Basic);
            throw e;
        }
    }

    @Test
    public void testHandleMessageInvalidMove() throws Exception {
        Game game = spy(new Game());
        Player player1 = new Player("Johan Vos", player1Session);
        game.setPlayer1(player1);
        Player player2 = new Player("Mike Seghers", player2Session);
        game.setPlayer2(player2);

        when(player1Session.getId()).thenReturn("sid1");
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player2Session.getId()).thenReturn("sid2");
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        when(game.registerMove(0, 0, player1)).thenReturn(false);

        handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "0");

        verify(game).registerMove(0, 0, player1);
        verify(player1Basic).sendText(TicTacToeMessage.INVALID_MOVE.getMessage() + " 0");
        verifyNoMoreInteractions(player1Basic, player2Basic);
    }

    @Test(expected = UnhandledMessageException.class)
    public void testHandleMessageIOExceptionOnMessage() throws Exception {
        Game game = spy(new Game());
        Player player1 = new Player("Johan Vos", player1Session);
        game.setPlayer1(player1);
        Player player2 = new Player("Mike Seghers", player2Session);
        game.setPlayer2(player2);

        when(player1Session.getId()).thenReturn("sid1");
        when(player1Session.getUserProperties()).thenReturn(userProperties);
        when(player1Session.getBasicRemote()).thenReturn(player1Basic);
        when(player2Session.getId()).thenReturn("sid2");
        when(player2Session.getBasicRemote()).thenReturn(player2Basic);
        when(userProperties.get(TicTacToeEndpoint.GAME_PROPERTY_KEY)).thenReturn(game);
        when(game.registerMove(0, 0, player1)).thenReturn(false);
        doThrow(new IOException()).when(player1Basic).sendText(TicTacToeMessage.INVALID_MOVE.getMessage() + " 0");

        try {
            handler.handleMessage(player1Session, TicTacToeMessage.PLAYER_MADE_MOVE, "0");


        } catch (UnhandledMessageException e) {
            verify(game).registerMove(0, 0, player1);
            verify(player1Basic).sendText(TicTacToeMessage.INVALID_MOVE.getMessage() + " 0");
            verifyNoMoreInteractions(player1Basic, player2Basic);

            assertThat(e.getCause(), is(instanceOf(IOException.class)));

            throw e;
        }


    }
}
