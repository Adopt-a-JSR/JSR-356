'use strict';

angular.module('tic-tac-toe.services', []).factory('socket', function ($rootScope) {
    var socket = new WebSocket('ws://localhost:8080/tictactoeserver/endpoint');
    return {
        onmessage: function (callback) {
            socket.onmessage = function (message) {
                $rootScope.$apply(function () {
                    callback(socket, message.data);
                });
            };
        },
        onclose: function (callback) {
            socket.onclose = function (closeEvent) {
                $rootScope.$apply(function() {
                   callback(socket, closeEvent.reason, closeEvent.code, closeEvent.wasClean);
                });
            }
        },
        send: function (data) {
            socket.send(data);
        }
    };
});
