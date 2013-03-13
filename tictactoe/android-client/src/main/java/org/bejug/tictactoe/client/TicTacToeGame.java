package org.bejug.tictactoe.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.bejug.tictactoe.client.websocket.TicTacToeWebsocketClient;
import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Filip Maelbrancke
 */
public class TicTacToeGame implements TicTacToeWebsocketClient.TicTacToeWSClientCallback {

    private static final String TAG = TicTacToeGame.class.getSimpleName();
    private static final String SERVER_URL = "ws://ec2-54-242-90-129.compute-1.amazonaws.com:80/tictactoeserver/endpoint";
    private static final String WS_MESSAGE = "org.bejug.tictactoe.client.websocket_message";
    private static final String WS_EVENT = "org.bejug.tictactoe.client.websocket_event";

    private GameState currentState = GameState.INIT;
    private TicTacToePossibility player = TicTacToePossibility.NONE;
    private TicTacToeGameCallback mCallback = sDummyCallback;
    private TicTacToeWebsocketClient wsClient;

    private TicTacToeCell[] positions = null;

    public enum GameState {
        INIT, WAITING, PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }

    public enum TicTacToePossibility {
        NONE, CROSS, NOUGHT
    }

    private enum WebsocketConnectionEvent {
        CONNECTED, CLOSE, ERROR
    }

    public enum CellState {
        NORMAL, WINNER, LOSER
    }

    public interface TicTacToeGameCallback {
        void onWebsocketConnectionChange(String state);
        void onGameStateChange(GameState gameState);
        void onGameBoardChange(TicTacToeCell[] positions);
    }

    public TicTacToeGame() {
        initGame();
    }

    private void initGame() {
        setCurrentState(GameState.INIT);
    }

    private void initializeGameState() {
        this.player = TicTacToePossibility.NONE;
        // initialize the tictactoe positions
        positions = new TicTacToeCell[9];
        for (int i = 0; i < 9; i++) {
            positions[i] = new TicTacToeCell(TicTacToePossibility.NONE, CellState.NORMAL);
        }
    }

    public void startGame() {
        initializeGameState();
        try {
            wsClient = new TicTacToeWebsocketClient(new URI(SERVER_URL), new Draft_17());
            wsClient.setTicTacToeWSClientCallback(this);
            wsClient.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "WebSocket exception: " + e.getMessage());
        }
        mCallback.onGameBoardChange(positions);
    }

    private void endGame() {
        wsClient.close();
    }

    public GameState getGameState() {
        return this.currentState;
    }

    public TicTacToeCell[] getPositions() {
        return this.positions;
    }

    public void onWebsocketConnectionEvent(final WebsocketConnectionEvent event, final String extra) {
        switch (event) {
            case CONNECTED:
                Log.d(TAG, "Websocket connection:: connected");
                mCallback.onWebsocketConnectionChange("CONNECTED");
                break;

            case CLOSE:
                Log.d(TAG, "Websocket connection:: closed: " + extra);
                mCallback.onWebsocketConnectionChange("CLOSED");
                break;

            case ERROR:
                Log.d(TAG, "Websocket connection:: error: " + extra);
                mCallback.onWebsocketConnectionChange("ERROR");
                break;
        }
    }

    public void onWebsocketMessageInput(final String msg) {
        Log.d(TAG, "WS message received:: " + msg);
        if ("p1".equalsIgnoreCase(msg)) {
            setCurrentState(GameState.WAITING);
            Log.d(TAG, "Waiting for other player");
        } else if ("p2".equalsIgnoreCase(msg)) {
            this.player = TicTacToePossibility.NOUGHT;
            setCurrentState(GameState.PLAYING);
            Log.d(TAG, "Game joined, playing p2 with O, and my turn");
        } else if ("p3".equalsIgnoreCase(msg)) {
            this.player = TicTacToePossibility.CROSS;
            setCurrentState(GameState.WAITING);
            Log.d(TAG, "Game joined, playing p1 with X, and other players turn");
        } else if (msg.startsWith("o")) {
            setCurrentState(GameState.PLAYING);
            final int position = Integer.parseInt(msg.substring(1));
            updateCells(position, TicTacToePossibility.NOUGHT);
        } else if (msg.startsWith("x")) {
            setCurrentState(GameState.PLAYING);
            final int position = Integer.parseInt(msg.substring(1));
            updateCells(position, TicTacToePossibility.CROSS);
        }
    }

    private void sendWebsocketMessage(final String msg) {
        if (wsClient != null) {
            wsClient.send(msg);
        }
    }

    private void updateCells(final int position, final TicTacToePossibility type) {
        this.currentState = GameState.PLAYING;
        this.positions[position] = new TicTacToeCell(type, CellState.NORMAL);
        checkForWinner(type);
        mCallback.onGameBoardChange(this.positions);
    }

    public void onPLayersInput(final int cellPosition) {
        if (currentState == GameState.PLAYING) {
            setCurrentState(GameState.WAITING);
            final String wsMessage = (this.player == TicTacToePossibility.CROSS) ? "x" + cellPosition : "o" + cellPosition;
            sendWebsocketMessage(wsMessage);
            updateCells(cellPosition, this.player);
        }
    }

    private void setCurrentState(final GameState gameState) {
        this.currentState = gameState;
        mCallback.onGameStateChange(gameState);
    }

    /**
     * Check if the game is a draw (i.e. no more empty cells)
     * @return boolean
     */
    private boolean isDraw() {
        for (TicTacToeCell position : positions) {
            if (position.getCellType() == TicTacToePossibility.NONE) {
                return false;
            }
        }
        return true;
    }

    private void checkForWinner(final TicTacToePossibility lastPlayedType) {
        CellState potentialCellState = (lastPlayedType.equals(this.player)) ? CellState.WINNER : CellState.LOSER;
        GameState potentialGameState = (lastPlayedType.equals(TicTacToePossibility.CROSS)) ?  GameState.CROSS_WON: GameState.NOUGHT_WON;
        for (int i = 0; i < 3; i++) {
            // horizontal
            if ( (positions[i * 3].getCellType() == lastPlayedType) && (positions[i * 3 + 1].getCellType() == lastPlayedType) && (positions[i * 3 + 2].getCellType() == lastPlayedType) ) {
                positions[i * 3].setCellState(potentialCellState);
                positions[i * 3 + 1].setCellState(potentialCellState);
                positions[i * 3 + 2].setCellState(potentialCellState);
                setCurrentState(potentialGameState);
                endGame();
                return;
            }
            // vertical
            if ( (positions[i].getCellType() == lastPlayedType) && (positions[i + 3].getCellType() == lastPlayedType) && (positions[i + 6].getCellType() == lastPlayedType) ) {
                positions[i].setCellState(potentialCellState);
                positions[i + 3].setCellState(potentialCellState);
                positions[i + 6].setCellState(potentialCellState);
                setCurrentState(potentialGameState);
                endGame();
                return;
            }
        }
        // diagonal
        if ( (positions[0].getCellType() == lastPlayedType) && (positions[4].getCellType() == lastPlayedType) && (positions[8].getCellType() == lastPlayedType) ) {
            positions[0].setCellState(potentialCellState);
            positions[4].setCellState(potentialCellState);
            positions[8].setCellState(potentialCellState);
            setCurrentState(potentialGameState);
            endGame();
            return;
        }
        // opposite diagonal
        if ( (positions[2].getCellType() == lastPlayedType) && (positions[4].getCellType() == lastPlayedType) && (positions[6].getCellType() == lastPlayedType) ) {
            positions[2].setCellState(potentialCellState);
            positions[4].setCellState(potentialCellState);
            positions[6].setCellState(potentialCellState);
            setCurrentState(potentialGameState);
            endGame();
            return;
        }
        if (isDraw()) {
            // set all cells to loser if the game is a draw
            for (TicTacToeCell cell : positions) {
                cell.setCellState(CellState.LOSER);
            }
            setCurrentState(GameState.DRAW);
            endGame();
        }
    }

    // Methods to handle the websocket events

    @Override
    public void onWSConnected() {
        mWSConnectionHandler.sendMessage(getWSEventMessage(WebsocketConnectionEvent.CONNECTED, null));
    }

    @Override
    public void onWSMessageReceived(String msg) {
        mWSMessageHandler.sendMessage(getWSStringMessage(msg));
    }

    @Override
    public void onWSClose(int code, String reason) {
        mWSConnectionHandler.sendMessage(getWSEventMessage(WebsocketConnectionEvent.CLOSE, reason));
    }

    @Override
    public void onWSError(Exception e) {
        mWSConnectionHandler.sendMessage(getWSEventMessage(WebsocketConnectionEvent.ERROR, e.getMessage()));
    }

    Handler mWSConnectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Bundle bundle = msg.getData();
            final WebsocketConnectionEvent _event = (WebsocketConnectionEvent) bundle.getSerializable(WS_EVENT);
            final String _extra = bundle.getString(WS_MESSAGE);
            onWebsocketConnectionEvent(_event, _extra);
        }
    };

    Handler mWSMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Bundle bundle = msg.getData();
            final String _msg = bundle.getString(WS_MESSAGE);
            onWebsocketMessageInput(_msg);
        }
    };

    private Message getWSStringMessage(final String msg) {
        final Bundle bundle = new Bundle();
        final Message message = mWSMessageHandler.obtainMessage();
        bundle.putString(WS_MESSAGE, msg);
        message.setData(bundle);
        return message;
    }

    private Message getWSEventMessage(final WebsocketConnectionEvent event, final String extraInfo) {
        final Bundle bundle = new Bundle();
        final Message message = mWSConnectionHandler.obtainMessage();
        bundle.putSerializable(WS_EVENT, event);
        bundle.putString(WS_MESSAGE, extraInfo);
        message.setData(bundle);
        return message;
    }

    public void setTicTacToeGameCallback(TicTacToeGameCallback callback) {
        if (callback == null) {
            mCallback = sDummyCallback;
        } else {
            mCallback = callback;
        }
    }

    private static TicTacToeGameCallback sDummyCallback = new TicTacToeGameCallback() {
        @Override
        public void onGameBoardChange(final TicTacToeCell[] positions) {

        }

        @Override
        public void onWebsocketConnectionChange(final String state) {

        }

        @Override
        public void onGameStateChange(final GameState gameState) {

        }
    };
}
