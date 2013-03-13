package org.bejug.tictactoe.client;

/**
 * @author Filip Maelbrancke
 */
public class TicTacToeCell {

    private TicTacToeGame.TicTacToePossibility cellType;
    private TicTacToeGame.CellState cellState;

    public TicTacToeCell(TicTacToeGame.TicTacToePossibility cellType, TicTacToeGame.CellState cellState) {
        this.cellType = cellType;
        this.cellState = cellState;
    }

    public TicTacToeGame.TicTacToePossibility getCellType() {
        return cellType;
    }

    public void setCellType(TicTacToeGame.TicTacToePossibility cellType) {
        this.cellType = cellType;
    }

    public TicTacToeGame.CellState getCellState() {
        return cellState;
    }

    public void setCellState(TicTacToeGame.CellState cellState) {
        this.cellState = cellState;
    }
}
