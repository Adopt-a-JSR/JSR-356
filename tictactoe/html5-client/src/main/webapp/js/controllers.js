'use strict';

function TicTacToeController($scope, socket) {
    var controller = this;
    var size = 3;

    $scope.message = "";

    $scope.active = false;
    $scope.myturn = false;
    $scope.symbol = "unknown";

    $scope.placeSymbol = function(row, column) {
        place(row, column, $scope.symbol);
        socket.send($scope.symbol +calculateCellNumber(row,column));
        toggleOtherPlayersTurn();
    }

    var init = function(){
        $scope.board = new GameBoard(size).board;
        $scope.active = false;
        $scope.myturn = false;
    };

    var calculateCellNumber = function(row, column) {
        return (row*size) + column;
    }

    var calculateCoordinate = function(cellNumber) {
       var column = cellNumber % size;
       var row = (cellNumber - column) / size;
       return {row: row, column: column};
    }

    var startGame = function(){
        $scope.active = true;
        $scope.message = "Game in progress";
    }

    var toggleOtherPlayersTurn = function () {
        $scope.myturn = false;
    }

    var toggleMyTurn = function () {
        $scope.myturn = true;
    }

    var waitingForOtherPlayerToJoin = function() {
       $scope.message = "Waiting for other Player to join";
    }

    var place = function(row, column, symbol) {
        $scope.board[row][column].content = symbol;
    }

    init();

    socket.onmessage(function(socket, result) {
        if(result == "p1")
                waitingForOtherPlayerToJoin();
        if (result == "p2") {
            $scope.symbol = "o";
            startGame();
            toggleMyTurn();
        }
        if (result == "p3") {
            $scope.symbol = "x";
            startGame();
            toggleOtherPlayersTurn();
        }
        if (result.charAt(0) == "o") {
            var coordinates = calculateCoordinate(result.charAt(1));
            place(coordinates.row, coordinates.column, "o");
            toggleMyTurn();
        }
        if (result.charAt(0) == "x") {
            var coordinates = calculateCoordinate(result.charAt(1));
            place(coordinates.row, coordinates.column, "x");
            toggleMyTurn();
        }
    });
}

function GameBoard(length) {
    this.board = [];

    for ( var i = 0; i < length; i++) {
        var row = [];
        for ( var j = 0; j < length; j++) {
            row.push({});
        }
        this.board.push(row);
    }
};