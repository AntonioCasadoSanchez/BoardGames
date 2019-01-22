var app = angular.module("GameApp", []);
var estado;
app.controller("GameController", function($scope, $http) {
	$scope.userName = "";
	$scope.pwd = "";
	$scope.estado = "";
	$scope.login2 = function(){
		var recurso="/login";
		var data= "userName=" + $scope.userName + "&pwd=" + $scope.pwd;
		var config = {
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
		};
		$http.post(recurso, data, config).then(
				function(response){
					sessionStorage.userName=response.data.userName;
					sessionStorage.email=response.data.email;
					loadPage("salaEspera.html");
				},
				function(response) {
					$scope.estado = "Usuario/Contraseña incorrectos";
				}
		);
	};
	function onSignin(googleUser){
		var profile = googleUser.getBasicProfile();
		console.log('ID: ' + profile.getId());
		console.log('Name:' + profile.getName());
		console.log('Image URL: ' + profile.getImageUrl());
		console.log('Email: ' + profile.getEmail());
	}
	function loadPage(url) {
		window.location.assign(url);
	};
	function add(texto, parametro) {
		$scope.estado = $scope.estado + texto;
		$scope.estado = $scope.estado + "hola!";
	};
	$scope.loadGames = function() {
		$http.get("/games").then(
			function(respuesta) {
				$scope.games=respuesta.data;
			}
		);
		sessionStorage.userName=response.userName;
	};
});
