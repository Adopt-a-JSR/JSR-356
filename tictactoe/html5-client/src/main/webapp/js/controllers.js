'use strict';

function TicTacToeController($scope, socket) {
    var controller = this;
    var size = 3;

    $scope.message = "";

    $scope.active = false;
    $scope.myturn = false;
    $scope.symbol = "unknown";
    $scope.otherSymbol = "unknown";

    $scope.placeSymbol = function(row, column) {
        place(row, column, $scope.symbol);
        socket.send("pm " + calculateCellNumber(row,column));
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
        if(result == "p1") {
                waitingForOtherPlayerToJoin();
        } else if (result.charAt(0) == "p" && result.charAt(1) == "2") {
            $scope.symbol = "o";
            $scope.otherSymbol = "x"
            startGame();
            toggleMyTurn();
        } else if (result == "p3") {
            $scope.symbol = "x";
            $scope.otherSymbol = "o"
            startGame();
            toggleOtherPlayersTurn();
        } else if (result.charAt(0) == "o" && result.charAt(1) == "m") {
            var coordinates = calculateCoordinate(result.charAt(3));
            place(coordinates.row, coordinates.column, $scope.otherSymbol);
            toggleMyTurn();
        } else {
            if (console && console.log) {
                console.log("Message received, but not evaluated", result)
            }
        }
    });

    socket.onclose(function(socket, reason, code, clean) {
        if (console && console.log) {
            console.log("Socket closed with reason, code and clean flag", reason, code, clean);
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