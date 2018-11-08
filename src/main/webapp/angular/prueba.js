var app = angular.module("GameApp", []);
app.controller("GameController", function($scope, $http) {
	$scope.userName = "";
	$scope.pwd = "";
	$scope.estado = "Desconectado";
	$scope.login = function() {
		$scope.estado = "Conectado";
	};
});