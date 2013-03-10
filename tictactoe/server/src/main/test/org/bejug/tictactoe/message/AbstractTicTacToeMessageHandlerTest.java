package org.bejug.tictactoe.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.mockito.Mockito.*;

/**
 * @author mike
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractTicTacToeMessageHandlerTest {

    @Mock
    private Session session;

    @Mock
    private RemoteEndpoint.Basic basic;

    private AbstractTicTacToeMessageHandler handler = new AbstractTicTacToeMessageHandler() {
        @Override
        public void handleMessage(Session session, TicTacToeMessage message, String... parameters) throws UnhandledMessageException {
            //NO-OP for test
        }
    };


    @Test(expected = NullPointerException.class)
    public void testSendMessageToPlayerNullParams() throws Exception {
        handler.sendMessageToPlayer(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testSendMessageToPlayerNullSession() throws Exception {
        handler.sendMessageToPlayer(TicTacToeMessage.INVALID_MESSAGE, null);
    }

    @Test(expected = NullPointerException.class)
    public void testSendMessageToPlayerNullMessage() throws Exception {
        handler.sendMessageToPlayer(null, session);
    }

    @Test
    public void testSendMessageToPlayerNoParams() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.sendMessageToPlayer(TicTacToeMessage.INVALID_MESSAGE, session);

        verify(basic).sendText(TicTacToeMessage.INVALID_MESSAGE.getMessage());
    }

    @Test
    public void testSendMessageToPlayerNullParam() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.sendMessageToPlayer(TicTacToeMessage.INVALID_MESSAGE, session, null);

        verify(basic).sendText(TicTacToeMessage.INVALID_MESSAGE.getMessage());
    }

    @Test
    public void testSendMessageToPlayerMoreParams() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.sendMessageToPlayer(TicTacToeMessage.INVALID_MESSAGE, session, "one", "two", "three");

        verify(basic).sendText(TicTacToeMessage.INVALID_MESSAGE.getMessage() + " one two three");
    }

    @Test
    public void testSendMessageToPlayerExtraPaddedParams() throws Exception {
        when(session.getBasicRemote()).thenReturn(basic);

        handler.sendMessageToPlayer(TicTacToeMessage.INVALID_MESSAGE, session, "  one  ", " two       ", " three     ");

        verify(basic).sendText(TicTacToeMessage.INVALID_MESSAGE.getMessage() + " one two three");
    }
}
