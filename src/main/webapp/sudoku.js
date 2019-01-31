var cont = 0;
function inicio() {
	controlSeguridad();
};
function controlSeguridad() {
	var req = new XMLHttpRequest();
	req.open("POST", "controlSeguridad");
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.onreadystatechange = function(response) {
		if (req.readyState == 4) {
			if (req.status == 200) {
				if (req.responseText != "true") {
					alert("Has llegado aqui de manera ilegal, por favor, inicia sesion.");
					window.location.assign("index.html");
				} else {
					abrirWS();
				}
			} else {
				alert("problema con la peticion http");
			}
		}
	};
	req.send();
};
function abrirWS() {
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
		if (data.TYPE == "WAIT") {
			alert(data.mensaje);
		} else if (data.TYPE == "PARTIDA") {
			empezarPartida(data);
		}else if (data.TYPE=="CERRAR"){
			  ws.onclose();
		  }
	}
	ws.onclose = function() {
		alert("Conexion cerrada por el servidor");
		window.location.assign("index.html");
	}
}
function joinGame() {
	var mensaje = {
		TYPE : "JUGAR",
		juego : "sudoku"
	}
	ws.send(JSON.stringify(mensaje));
}
function empezarPartida(data) {
	alert("La partida entre " + data.Player1 + " y " + data.Player2
			+ " va a comenzar");
	timerOn();
	setInterval('timerOn()',2000);
	var mensaje = {
			TYPE : "SUDOKU",
			funcion : "cargar",
			player1: data.Player1,
			player2: data.Player2,
			
		}
		ws.send(JSON.stringify(mensaje));
}
function timerOn(){
	var contador = document.getElementById("timer");
	var hours = 00;
	var minutes = 00;
	var seconds = cont;
	if(cont>59){
		var aux = Math.floor(cont/60);
		seconds = cont - (60*aux);
		minutes = aux;
		if(minutes>59){
			var aux2=60*24;
			var aux3= Math.floor(aux/aux2);
			minutes = aux - (60*aux3);
			hours=aux3;
		}
		
	}
	contador.innerHTML = hours + ":"
	  + minutes + ":" + seconds;
	cont++;
}