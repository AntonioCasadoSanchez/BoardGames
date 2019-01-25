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
	function loadPage(url) {
		window.location.assign(url);
	};
	function add(texto, parametro) {
		$scope.estado = $scope.estado + texto;
		$scope.estado = $scope.estado + "hola!";
	};
});

function onSignIn(googleUser){
	var profile = googleUser.getBasicProfile();		
	var idGoogle= profile.getId();
	var nombre= profile.getName();
	var email= profile.getEmail();
	
	var req= new XMLHttpRequest();
	req.open("POST", "registarOloguear");
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.onreadystatechange = function(){
		if(request.readyState==4 && request.status==200){
			sessionStorage.userName=response.data.userName;
			sessionStorage.email=response.data.email;
			loadPage("salaEspera.html");
		}else{
			alert("Problema iniciando sesion con Google");
		}
	};
	var p="idGoogle=" + idGoogle + "&nombre=" + nombre + "&email=" + email;
	req.send(p);
};