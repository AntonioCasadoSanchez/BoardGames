var app = angular.module("GameApp", []);
var estado;
app.controller("GameController", function($scope, $http) {
	$scope.userName = "";
	$scope.pwd = "";
	$scope.estado = "Desconectado";
	$scope.login = function() {
		if ($scope.pwd == $scope.userName + "123") {// eSTA Comprobacion va aqui
													// o la tiene que hacer en
													// la Bdatos?
			var request = new XMLHttpRequest();
			request.open("GET", "http://localhost:8080/login?userName="
					+ $scope.userName + "&pwd=" + $scope.pwd);
			request.onreadystatechange = function() {
				if (request.readyState == 4) {
					add(request.responseText);// esto que es?
					ws = new WebSocket("ws://localhost:8080/gamews");
					ws.onopen = function() {

						//$("#contenido").load("salaEspera2.html");
						window.location.assign("salaEspera2.html");
					}
					ws.onerror = function() {
						add("Error al conectar WS");
					}
					ws.onmessage = function(message) {
						var data = message.data;
						data = JSON.parse(data);
						if (data.TYPE == "MATCH") {
							add(JSON.stringify(data));
							$("#contenido").load("salaEspera.html");
							// window.location.href =
							// "http://webpub.esi.uclm.es/spa";
							// window.location.href = "salaEspera.html";
						} else
							add("Mensaje desconocido");
					}
				}
			};
			request.send();// cuando y que envia esto

		} else {
			$scope.estado = "Error";
		}

	};
	$scope.login2 = function() {
		if ($scope.pwd == $scope.userName + "123") {
			$scope.estado = "El user es: " + $scope.userName + "La pwd es: "
					+ $scope.pwd;
		} else {
			$scope.estado = "Error";
		}
	};
	function add(texto) {
		$scope.estado = $scope.estado + texto;
		$scope.estado = $scope.estado + "hola!";
	}
	$scope.loadGames = function() {
		$http.get("/games").then(
			function(respuesta) {
				$scope.games=respuesta.data;
			}
		);
		sessionStorage.userName=response.userName;
	};
});