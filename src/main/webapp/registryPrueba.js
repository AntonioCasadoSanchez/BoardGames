var app = angular.module("GameApp", []);
var estado;
app.controller("GameController", function($scope, $http) {
	$scope.userName = "";
	$scope.pwd1 = "";
	$scope.pwd2 = "";
	$scope.email = "";
	$scope.estado = "";
	$scope.register = function(){
		var recurso="/register";
		var data= "email=" + $scope.email + "&userName=" + $scope.userName + "&pwd1=" + $scope.pwd1 + "&pwd2=" + $scope.pwd2;
		var config = {
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				}
		};
		$http.post(recurso, data, config).then(
				function(response){
					sessionStorage.setItem('email', email.value);
					alert("Registro correcto");
					loadPage("index.html");
				},
				function(response) {
					$scope.estado = "Mostrar el error aqui y no en la consola";
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
function subirFoto(){
	var file=fileToUpload.files[0];
	var reader=new FileReader();
	
	reader.onload=function(e){
		var blob = new Blob([reader.result, 'P']);
		var blobReader = new FileReader();
		blobReader.onload = function(event) {
			var buffer = event.target.result;
			chat.send(buffer);
		};
		blobReader.readAsArrayBuffer(blob);
	}
	reader.readAsArrayBuffer(file);
}



