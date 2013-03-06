'use strict';


// Declare app level module which depends on filters, and services
angular.module('tic-tac-toe', ['tic-tac-toe.services']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/game', {templateUrl: 'partials/game.html', controller: TicTacToeController});
    $routeProvider.otherwise({redirectTo:'/game'});
}]);
