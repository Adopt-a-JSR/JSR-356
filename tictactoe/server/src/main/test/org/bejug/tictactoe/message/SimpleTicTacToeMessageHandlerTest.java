package org.bejug.tictactoe.message;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author mike
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleTicTacToeMessageHandlerTest {
    private SimpleTicTacToeMessageHandler handler;

    @Mock
    private Session session;

    @Mock
    private RemoteEndpoint.Basic basic;

    @Before
    public void setUp() {
        handler = new SimpleTicTacToeMessageHandler();
    }

    @Test
    public void testHandleMessageNoParams() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.handleMessage(session, TicTacToeMessage.GAME_HAS_WINNER);

        verify(basic).sendText("p4");

    }

    @Test
    public void testHandleMessageNullParams() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.handleMessage(session, TicTacToeMessage.JOINED_AS_FIRST_PLAYER, null);

        verify(basic).sendText("p1");
    }

    @Test
    public void testHandleMessageWithParams() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.handleMessage(session, TicTacToeMessage.JOINED_AS_LAST_PLAYER, "1", "2", "3");

        verify(basic).sendText("p3 1 2 3");
    }

    @Test(expected = UnhandledMessageException.class)
    public void testHandleMessageSendFails() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);
        doThrow(new IOException()).when(basic).sendText("p4");

        try {
            handler.handleMessage(session, TicTacToeMessage.GAME_HAS_WINNER);
        } catch (UnhandledMessageException e) {
            verify(basic).sendText("p4");
            assertThat(e.getCause(), is(instanceOf(IOException.class)));
            throw e;
        }
    }
}
