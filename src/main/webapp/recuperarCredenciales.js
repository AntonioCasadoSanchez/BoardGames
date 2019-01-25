var app = angular.module("GameApp", []);
app.controller("GameController", function($scope, $http) {
	$scope.getUser = function(){
		var usuario = document.getElementById("userName").value;
	    window.location.href = "http://localhost:8080/solicitarToken?userName="+usuario.toString();  
	  }
	};    
}