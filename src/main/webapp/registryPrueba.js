var app = angular.module("GameApp", []);
var estado;
app.controller("GameController", function($scope, $http) {
	$scope.userName = "";
	$scope.pwd = "";
	$scope.pwd2 = "";
	$scope.email = "";
	$scope.estado = "Sin enviar";
	$scope.register = function() {
		var request = new XMLHttpRequest();
		request.open("GET", "http://localhost:8080/register?email="
				+ $scope.email + "&userName=" + $scope.userName + "&pwd1="
				+ $scope.pwd + "&pwd2=" + $scope.pwd2);
		// request.setRequestHeader('Content-type',
		// 'application/x-www-form-urlencoded');
		$scope.estado = "Conectando";
		request.onreadystatechange = function() {
			if (request.readyState == 4) {
				add(request.responseText);// esto que es?
				ws = new WebSocket("ws://localhost:8080/gamews");
				ws.onopen = function() {
					$scope.estado = "Conectado";
				}
				ws.onerror = function() {
					add("Error al conectar WS");
				}
				ws.onmessage = function(message) {
					var data = message.data;
					data = JSON.parse(data);
					$scope.estado(data);
				}
			}
		};
		// var datos= $scope.email +"&" + $scope.userName + "&"+ $scope.pwd +
		// "&" + $scope.pwd2;
		// $scope.estado=datos;
		// request.send(datos);// cuando y que envia esto
		request.send();
	};
	
	function add(texto) {
		$scope.estado = $scope.estado + texto;
	}
});
