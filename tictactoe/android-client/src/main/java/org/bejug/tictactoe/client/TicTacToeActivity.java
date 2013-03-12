package org.bejug.tictactoe.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * @author Filip Maelbrancke
 */
public class TicTacToeActivity extends Activity implements TicTacToeGame.TicTacToeGameCallback, TicTacToeView.TicTacToeViewCallback {

    private static final String TAG = TicTacToeActivity.class.getSimpleName();
    private static final String STATE_WS = "websocketState";
    private static final String STATE_GAME = "gameState";

    @Inject
    TicTacToeGame game;

    private TicTacToeView board;
    private TextView websocketState;
    private TextView gameState;
    private Button gameControlButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ((TicTacToeApplication) getApplication()).inject(this);
        setContentView(R.layout.main);

        board = (TicTacToeView) findViewById(R.id.tictactoe_board);
        websocketState = (TextView) findViewById(R.id.ws_state);
        gameState = (TextView) findViewById(R.id.game_state);
        gameControlButton = (Button) findViewById(R.id.game_control_button);
        gameControlButton.setOnClickListener(gameControlButtonListener);

        game.setTicTacToeGameCallback(this);
        board.setTicTacToeViewCallback(this);

        final TicTacToeGame.GameState state = game.getGameState();
        if (state == TicTacToeGame.GameState.INIT
                || state == TicTacToeGame.GameState.DRAW
                || state == TicTacToeGame.GameState.CROSS_WON
                || state == TicTacToeGame.GameState.NOUGHT_WON) {
            enableGameControlButton();
        }
        board.setBoardCells(game.getPositions());

        if (savedInstanceState != null) {
            gameState.setText(savedInstanceState.getCharSequence(STATE_GAME));
            websocketState.setText(savedInstanceState.getCharSequence(STATE_WS));
        }
    }

    @Override
    public void onGameBoardChange(TicTacToeCell[] positions) {
        board.setBoardCells(positions);
    }

    @Override
    public void onWebsocketConnectionChange(String state) {
        websocketState.setText(state);
    }

    @Override
    public void onGameStateChange(TicTacToeGame.GameState state) {
        gameState.setText(state.name());
        if (state == TicTacToeGame.GameState.DRAW
                || state == TicTacToeGame.GameState.CROSS_WON
                || state == TicTacToeGame.GameState.NOUGHT_WON) {
            enableGameControlButton();
        }
    }

    @Override
    public void onUserClickedCell(int position) {
        game.onPLayersInput(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATE_GAME, gameState.getText());
        outState.putCharSequence(STATE_WS, websocketState.getText());
    }

    private void enableGameControlButton() {
        gameControlButton.setVisibility(View.VISIBLE);
    }

    View.OnClickListener gameControlButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            game.startGame();
            gameControlButton.setVisibility(View.GONE);
        }
    };
}

