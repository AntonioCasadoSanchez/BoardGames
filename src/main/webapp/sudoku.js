var cont = 0;
var idPartida;
var oponente;
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
		} else if (data.TYPE == "TABLEROINICIAL") {
			cargarTableroInicial(data.tablero);
			cargarTableroOponente(data.tablero);
		} else if (data.TYPE == "MARCAR"){
			marcar(data.coordI, data.coordJ);
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
	idPartida= data.id;
	timerOn();
	setInterval('timerOn()', 2000);
	var mensaje = {
		TYPE : "SUDOKU",
		funcion : "cargar",
		player1 : data.Player1,
		player2 : data.Player2,

	}
	ws.send(JSON.stringify(mensaje));
}
function timerOn() {
	var contador = document.getElementById("timer");
	var hours = 00;
	var minutes = 00;
	var seconds = cont;
	if (cont > 59) {
		var aux = Math.floor(cont / 60);
		seconds = cont - (60 * aux);
		minutes = aux;
		if (minutes > 59) {
			var aux2 = 60 * 24;
			var aux3 = Math.floor(aux / aux2);
			minutes = aux - (60 * aux3);
			hours = aux3;
		}

	}
	contador.innerHTML = hours + ":" + minutes + ":" + seconds;
	cont++;
}
function cargarTableroInicial(tablero) {
	var tablerito = tablero.toString();
	var valor = "";
	var i, j, cont = 0;
	var id = "c";
	var casilla;
	for (i = 0; i < 9; i++) {
		for (j = 0; j < 9; j++) {
			var res = id.concat(i).concat(j);
			casilla = document.getElementById(res);
			valor = tablerito.charAt(cont);
			if (valor == "0") {
				valor = "";
			}else{
				casilla.style.color = "rgb(54, 114, 163)";
				casilla.readOnly = true;
			}
			casilla.value = valor;
			cont++;
		}
	}
}
function cargarTableroOponente(tablero) {
	var tablainicial = tablero.toString();
	var valor = "";
	var i, j, cont = 0;
	var id = "co";
	var casilla;
	for (i = 0; i < 9; i++) {
		for (j = 0; j < 9; j++) {
			var res = id.concat(i).concat(j);
			casilla = document.getElementById(res);
			valor = tablainicial.charAt(cont);
			if (valor == "0") {
				valor = "";
			} else {
				valor = "X";
			}
			casilla.value = valor;
			cont++;
		}
	}
}
function Foco(x) {
	x.style.background = "#E4EAEF";
}
function NoFoco(x) {
	x.style.background = "#FFFFFF";
}
function KeyUp(x){
	var valor = x.value;
	var id = x.id;
	var idI = id.charAt(1); 
	var idJ = id.charAt(2);
	if(isNaN(valor)){
		x.value="";
	}else{
		ComprobarLinea(valor, id, idI);
		ComprobarColumna(valor, id, idJ);
		ComprobarCuadrado(valor, id, idI, idJ);
		EnviarDato(idI,idJ);
	}
}
function ComprobarLinea(valor, id, idI){
	var j; cont = 0;
	var casilla, res;
	var c = "c";
	for(j=0; j<9; j++){
		res = c.concat(idI).concat(j);
		casilla = document.getElementById(res);
		if(casilla.value == valor){
			cont++;
		}
	}
	if(cont>1){
		for(j=0; j<9; j++){
			res = c.concat(idI).concat(j);
			casilla = document.getElementById(res);
			casilla.style.background = "#FFE0C1";
		}
	}else{
		for(j=0; j<9; j++){
			res = c.concat(idI).concat(j);
			casilla = document.getElementById(res);
			casilla.style.background = "white";
		}
	}
}
function ComprobarColumna(valor, id, idJ){
	var i; cont = 0;
	var casilla, res;
	var c = "c";
	for(i=0; i<9; i++){
		res = c.concat(i).concat(idJ);
		casilla = document.getElementById(res);
		if(casilla.value == valor){
			cont++;
		}
	}
	if(cont>1){
		for(i=0; i<9; i++){
			res = c.concat(i).concat(idJ);
			casilla = document.getElementById(res);
			casilla.style.background = "#FFE0C1";
		}
	}else{
		for(i=0; i<9; i++){
			res = c.concat(i).concat(idJ);
			casilla = document.getElementById(res);
			casilla.style.background = "white";
		}
	}
}
function ComprobarCuadrado(valor, id, idI, idJ){
	
}
function EnviarDato(idI, idJ){
	var mensaje = {
			TYPE : "SUDOKU",
			funcion : "marcar",
			coordI : idI,
			coordJ : idJ,
			matchID : idPartida
		}
		ws.send(JSON.stringify(mensaje));
}
function marcar(coordI, coordJ){
	var valor = "";
	var i, j, cont = 0;
	var id = "co";
	var res= id.concat(coordI).concat(coordJ);
	var casilla = document.getElementById(res);
	casilla.value="X";
}

