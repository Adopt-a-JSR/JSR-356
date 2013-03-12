package org.bejug.tictactoe.client;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Filip Maelbrancke
 */
public class TicTacToeView extends View {

    private static final String TAG = TicTacToeView.class.getSimpleName();

    private static final int LINE_STROKE_WIDTH = 5;
    private static final int X_O_STROKE_WIDTH = 10;
    private static final int BOARD_COLUMNS = 3;
    private Paint mPaint;
    private TicTacToeCell[] boardCells = null;
    private TicTacToeViewCallback mCallback = sDummyCallback;

    public interface TicTacToeViewCallback {
        void onUserClickedCell(int position);
    }

    public TicTacToeView(Context context) {
        this(context, null);
    }

    public TicTacToeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicTacToeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }

    public void setBoardCells(TicTacToeCell[] cells) {
        boardCells = cells;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);

        final int size = getBoardSize();
        final int horizontalOffset = getHorizontalOffset();
        final int verticalOffset = getVerticalOffset();
        final int cellSize = getCellSize();

        mPaint.setColor(Color.DKGRAY);
        mPaint.setStrokeWidth(LINE_STROKE_WIDTH);
        for (int columnLine = 0; columnLine < (BOARD_COLUMNS-1); columnLine++) {
            int cx = horizontalOffset + ( (columnLine+1) * cellSize );
            canvas.drawLine(cx, verticalOffset, cx, verticalOffset + size, mPaint);
        }
        for (int rowLine = 0; rowLine < (BOARD_COLUMNS-1); rowLine++) {
            int cy = verticalOffset + ( (rowLine + 1) *  cellSize);
            canvas.drawLine(horizontalOffset, cy, horizontalOffset + size, cy, mPaint);
        }
        int inset = (int) (cellSize * 0.1);

        if (boardCells != null) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(X_O_STROKE_WIDTH);

            for (int i = 0; i < BOARD_COLUMNS; i++) {
                for (int j = 0; j < BOARD_COLUMNS; j++) {
                    Rect rect = new Rect( (horizontalOffset + (i * cellSize)) + inset,
                            (verticalOffset + (j * cellSize)) + inset,
                            ((horizontalOffset + (i * cellSize)) + cellSize) - inset,
                            ((verticalOffset + (j * cellSize)) + cellSize) - inset
                            );
                    final TicTacToeCell cell = boardCells[getPosition(i, j)];
                    if (cell.getCellState() == TicTacToeGame.CellState.WINNER) {
                        mPaint.setColor(Color.GREEN);
                    } else if (cell.getCellState() == TicTacToeGame.CellState.LOSER) {
                        mPaint.setColor(Color.RED);
                    } else {
                        mPaint.setColor(Color.WHITE);
                    }
                    if (cell.getCellType() == TicTacToeGame.TicTacToePossibility.NOUGHT) {
                        canvas.drawCircle((rect.right + rect.left) / 2,
                                (rect.bottom + rect.top) / 2,
                                (rect.right - rect.left) / 2,
                                mPaint);
                    }
                    else if (cell.getCellType() == TicTacToeGame.TicTacToePossibility.CROSS) {
                        canvas.drawLine( rect.left, rect.top, rect.right, rect.bottom, mPaint );
                        canvas.drawLine( rect.left, rect.bottom, rect.right, rect.top, mPaint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }
        final int horizontalOffset = getHorizontalOffset();
        final int verticalOffset = getVerticalOffset();
        final int cellSize = getCellSize();
        for (int i = 0; i < BOARD_COLUMNS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                Rect r = new Rect( horizontalOffset + (i * cellSize),
                        verticalOffset + (j * cellSize),
                        (horizontalOffset + (i * cellSize)) + cellSize,
                        (verticalOffset + (j * cellSize)) + cellSize  );
                if (r.contains( (int)event.getX(), (int)event.getY() )) {
                    Log.d("tictactoe", "Touch in " + getPosition(i, j) );
                    mCallback.onUserClickedCell(getPosition(i, j));
                    return true;
                }
            }
        }
        return true;
    }

    private int getPosition(final int i, final int j) {
        return (j * BOARD_COLUMNS) + i;
    }

    private int getBoardSize() {
        final int sizeToCheck = (getWidth() < getHeight()) ? getWidth() : getHeight();
        return (int) (sizeToCheck * 0.8);
    }

    private int getHorizontalOffset() {
        return ( getWidth() / 2 ) - ( getBoardSize() / 2 );
    }

    private int getVerticalOffset() {
        return ( getHeight() / 2 ) - ( getBoardSize() / 2 );
    }

    private int getCellSize() {
        return ( getBoardSize() / 3 );
    }

    public void setTicTacToeViewCallback(TicTacToeViewCallback callback) {
        if (callback == null) {
            mCallback = sDummyCallback;
        } else {
            mCallback = callback;
        }
    }

    private static TicTacToeViewCallback sDummyCallback = new TicTacToeViewCallback() {
        @Override
        public void onUserClickedCell(final int position) {
            Log.d(TAG, "User clicked on cell " + position);
        }
    };
}
