function inicio() {
	controlSeguridad();
	ws = new WebSocket("ws://localhost:8080/gamews");
	ws.onopen = function() {
		joinGame();
	}
	ws.onerror = function() {
		alert("Error al conectar WS");
	}
	ws.onmessage = function(message) {
		var data = message.data;
		data = JSON.parse(data);
		if(data.TYPE=="WAIT"){
			  alert(data.mensaje);
		  }
	}
	ws.onclose = function(){
		alert("adios");
	}
};
function controlSeguridad() {
	var req= new XMLHttpRequest();
	req.open("POST", "controlSeguridad");
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.onreadystatechange = function(response){
		if(req.readyState==4){
			if(req.status==200){
				if(!response.returnValue){
					alert("Has llegado aqui de manera ilegal, por favor, inicia sesion.");
					window.location.assign("index.html");
				}
			}else{
				alert("problema con la peticion http");
			}
		}
	};
	req.send();
};
function joinGame(){
	var mensaje={
			TYPE : "JUGAR",
			juego : "sudoku",
	}
	ws.send(JSON.stringify(mensaje));
}