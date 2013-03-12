package org.bejug.tictactoe.client.websocket;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author Filip Maelbrancke
 */
public class TicTacToeWebsocketClient extends WebSocketClient {

    private static final String TAG = TicTacToeWebsocketClient.class.getSimpleName();

    private TicTacToeWSClientCallback mCallback = sDummyCallback;

    public interface TicTacToeWSClientCallback {
        void onWSMessageReceived(String msg);
        void onWSConnected();
        void onWSClose(int code, String reason);
        void onWSError(Exception e);
    }

    public TicTacToeWebsocketClient(URI serverURI) {
        super(serverURI);
    }

    public TicTacToeWebsocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        mCallback.onWSConnected();
    }

    @Override
    public void onMessage(String s) {
        mCallback.onWSMessageReceived(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        mCallback.onWSClose(i, s);
    }

    @Override
    public void onError(Exception e) {
        mCallback.onWSError(e);
    }

    public void setTicTacToeWSClientCallback(TicTacToeWSClientCallback clbck) {
        if (clbck == null) {
            mCallback = sDummyCallback;
        } else {
            mCallback = clbck;
        }
    }

    private static TicTacToeWSClientCallback sDummyCallback = new TicTacToeWSClientCallback() {
        @Override
        public void onWSMessageReceived(String msg) {
            Log.d(TAG, "onMessage:: " + msg);
        }

        @Override
        public void onWSConnected() {
            Log.d(TAG, "onConnected");
        }

        @Override
        public void onWSClose(int code, String reason) {
            Log.d(TAG, "onClode:: code = " + code + " / reason = " + reason);
        }

        @Override
        public void onWSError(Exception e) {
            Log.d(TAG, "onError:: " + e.getMessage());
        }
    };
}
