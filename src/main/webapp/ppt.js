var cont = 0;
var idPartida;
var NombreUsuario;
var ronda = 0;
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
		} else if (data.TYPE == "CERRAR") {
			ws.onclose();
		} else if (data.TYPE == "WAITOPPONENT") {
			waitOpponent(data);
		} else if (data.TYPE == "RESOLVERJUGADAWIN") {
			resolverJugadaW(data);
		} else if (data.TYPE == "RESOLVERJUGADALOSE") {
			resolverJugadaL(data);
		} else if (data.TYPE == "RESOLVERJUGADATIE"){
			resolverJugadaT(data);
		} else if (data.TYPE == "FINWIN"){
			alert("Has ganado la partida, ENHORABUENA!!");
			ws.onclose();
		} else if (data.TYPE == "FINLOSE"){
			alert("Has perdido la partida, lo sentimos");
			ws.onclose();
		} else if (data.TYPE == "FINTIE"){
			alert("Has empatado la partida, mejor suerte la proxima vez");
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
		juego : "Piedra, papel, tijera",
	}
	ws.send(JSON.stringify(mensaje));
}
function empezarPartida(data) {
	alert("La partida entre " + data.Player1 + " y " + data.Player2
			+ " va a comenzar");
	NombreUsuario = data.Tu;
	var player1 = document.getElementById("cej1");
	var player2 = document.getElementById("cej2");
	if(NombreUsuario == data.Player1){
		player1.value = data.Player1;
		player2.value = data.Player2;
	}else{
		player1.value = data.Player2;
		player2.value = data.Player1;
	}
	
	idPartida = data.id;
	
	document.getElementById("piedra").disabled = false;
	document.getElementById("papel").disabled = false;
	document.getElementById("tijera").disabled = false;

}
function tirar(n) {
	var mensaje = {
		TYPE : "PPT",
		funcion : "tirada",
		matchID : idPartida,
		opcion : n
	}
	ws.send(JSON.stringify(mensaje));
}
function waitOpponent(data) {
	document.getElementById("estado").innerHTML = "Esperando Eleccion del Oponente";
	document.getElementById("piedra").disabled = true;
	document.getElementById("papel").disabled = true;
	document.getElementById("tijera").disabled = true;

}
function resolverJugadaW(data) {
	var opc1 = data.win;
	var opc2 = data.lose;
	document.getElementById("estado").innerHTML = opc1 + " vs " + opc2
			+ ", TU GANAS!";
	cargarImagen(opc1);
	cargarImagenOponente(opc2);
	document.getElementById("piedra").disabled = false;
	document.getElementById("papel").disabled = false;
	document.getElementById("tijera").disabled = false;
	cargarTablero("VICTORIA");
	cargarTableroOponente("DERROTA");
}
function resolverJugadaL(data) {
	var opc1 = data.win;
	var opc2 = data.lose;
	document.getElementById("estado").innerHTML = opc2 + " vs " + opc1
			+ ", HAS PERDIDO!";
	cargarImagen(opc2);
	cargarImagenOponente(opc1);
	document.getElementById("piedra").disabled = false;
	document.getElementById("papel").disabled = false;
	document.getElementById("tijera").disabled = false;
	cargarTablero("DERROTA");
	cargarTableroOponente("VICTORIA");
}
function resolverJugadaT(data) {
	var opc1 = data.opcion;
	document.getElementById("estado").innerHTML = opc1 + " vs " + opc1
			+ ", EMPATE!";
	cargarImagen(opc1);
	cargarImagenOponente(opc1);
	document.getElementById("piedra").disabled = false;
	document.getElementById("papel").disabled = false;
	document.getElementById("tijera").disabled = false;
	cargarTablero("EMPATE");
	cargarTableroOponente("EMPATE");
}
function cargarImagen(opc) {
	var img= document.getElementById("jugadaUser");
	if (opc == "Piedra") {
		img.src = "/imagenes/piedra.JPG";
	} else if (opc == "Papel") {
		img.src = "/imagenes/papel.JPG";
	} else {
		img.src = "/imagenes/tijera.JPG";
	}
}
function cargarImagenOponente(opc) {
	var img = document.getElementById("jugadaOponente");
	if (opc == "Piedra") {
		img.src = "/imagenes/piedra.JPG";
	} else if (opc == "Papel") {
		img.src = "/imagenes/papel.JPG";
	} else {
		img.src = "/imagenes/tijera.JPG";
	}
}
function cargarTablero(resultado){
	var casilla;
	if(ronda == 0){
		casilla = document.getElementById("ce01");
		casilla.value = resultado;
	}else if (ronda == 1){
		casilla = document.getElementById("ce02");
		casilla.value = resultado;
	}else{
		casilla = document.getElementById("ce03");
		casilla.value = resultado;
	}
}
function cargarTableroOponente(resultado){
	var casilla;
	if(ronda == 0){
		casilla = document.getElementById("ce04");
		casilla.value = resultado;
	}else if (ronda == 1){
		casilla = document.getElementById("ce05");
		casilla.value = resultado;
	}else{
		casilla = document.getElementById("ce06");
		casilla.value = resultado;
	}
	ronda++;
	if(FinPartida()){
		var mensaje = {
				TYPE : "PPT",
				funcion : "FinPartida",
				matchID : idPartida,
			}
			ws.send(JSON.stringify(mensaje));
	}
}
function FinPartida() {
	if(ronda == 3){
		return true;
	}else{
		return false;
	}
};
