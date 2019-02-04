var cont = 0;
var idPartida;
var oponente;
var tableroini;
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
			var mensaje = document.getElementById("mensaje");
			mensaje.innerHTML =data.mensaje;
		} else if (data.TYPE == "PARTIDA") {
			empezarPartida(data);
		} else if (data.TYPE == "CERRAR") {
			ws.onclose();
		} else if (data.TYPE == "TABLEROINICIAL") {
			cargarTableroInicial(data.tablero);
			cargarTableroOponente(data.tablero);
		} else if (data.TYPE == "MARCAR") {
			marcar(data.coordI, data.coordJ);
		} else if (data.TYPE == "LIMPIAR"){
			cargarTableroOponente(tableroini);
		} else if (data.TYPE == "WIN"){
			alert("Ganaste la partida! Enhorabuena");
			ws.onclose();
		} else if (data.TYPE == "LOSE"){
			alert("Perdiste la partida, lo sentimos");
			ws.onclose();
		} else if (data.TYPE == "RESOLVERFALLIDO"){
			alert("La solucion que propones no es correcta, sigue intentandolo");
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
	//alert("La partida entre " + data.Player1 + " y " + data.Player2 + " va a comenzar");
	var mensaje = document.getElementById("mensaje");
	mensaje.innerHTML = "";
	NombreUsuario = data.Tu;
	var player1 = document.getElementById("yourName");
	var player2 = document.getElementById("rivalName");
	if(NombreUsuario == data.Player1){
		player1.innerHTML = data.Player1;
		player2.innerHTML = data.Player2;
	}else{
		player1.innerHTML = data.Player2;
		player2.innerHTML = data.Player1;
	}
	idPartida = data.id;
	timerOn();
	setInterval('timerOn()', 2000);
	document.getElementById("limpiar").disabled = false;
	document.getElementById("resolver").disabled = false;
	document.getElementById("rendirse").disabled = false;
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
	tableroini = tablero;
	var tablerito = tablero.toString();
	var valor = "";
	var i, j, conta = 0;
	var id = "c";
	var casilla;
	for (i = 0; i < 9; i++) {
		for (j = 0; j < 9; j++) {
			var res = id.concat(i).concat(j);
			casilla = document.getElementById(res);
			valor = tablerito.charAt(conta);
			if (valor == "0") {
				valor = "";
			} else {
				casilla.style.color = "rgb(54, 114, 163)";
				casilla.readOnly = true;
			}
			casilla.value = valor;
			conta++;
		}
	}
}
function cargarTableroOponente(tablero) {
	var tablainicial = tablero.toString();
	var valor = "";
	var i, j, conta = 0;
	var id = "co";
	var casilla;
	for (i = 0; i < 9; i++) {
		for (j = 0; j < 9; j++) {
			var res = id.concat(i).concat(j);
			casilla = document.getElementById(res);
			valor = tablainicial.charAt(conta);
			if (valor == "0") {
				valor = "";
			} else {
				valor = "X";
			}
			casilla.value = valor;
			conta++;
		}
	}
}
function Foco(x) {
	x.style.background = "#E4EAEF";
}
function NoFoco(x) {
	x.style.background = "#FFFFFF";
}
function KeyUp(x) {
	var valor = x.value;
	var id = x.id;
	var idI = id.charAt(1);
	var idJ = id.charAt(2);
	if (isNaN(valor)) {
		x.value = "";
	} else {
		ComprobarLinea(valor, id, idI);
		ComprobarColumna(valor, id, idJ);
		ComprobarCuadrado(valor, id, idI, idJ);
		if (valor != "") {
			EnviarDato(idI, idJ);
		}// COMPROBAR ESTE ULTIMO IF--> TELEGRAM MIS COSAS. incluso se podria
		// poner el if justo debajo del ese (linea 155)???
		// Creo que esto no lo manda, solo no pone X. habria que quitar la X si
		// el valor es "", por tanto habria uqe llamar
		// a la funcion de enviar dato y añadir un modulo donde, si el valor es
		// "", en vez de funcion: marcar sea
		// funcion : desmarcar y en WS añadir modulo en texthandlemessage con
		// desmarcar y llamar al metodo manejador Sudoku
		// que compruebe que esa funcion es desmarcar y mande mensaje con TYPE:
		// DESMARCAR en vez de marcar. Por ultimo, en el cliente
		// Añadir funcion en @OnMessage para que, si el valor es "" en las
		// coordenadas se ponga "" en vez de X
		// Seguir pensando si el if puesto ahora mismo (linea 158) podria ir de
		// la 155 a la 157 porque ahi si que podria ser util
		// Todo esto quizas se puede hacer mas sencillo si enviamos con la
		// opcion MARCAR el dato que ponemos, y en los mismo metodos controlar
		// que si es "", se ponga "" en vez de X.
	}
}
function ComprobarLinea(valor, id, idI) {
	var j;
	cont = 0;
	var casilla, res;
	var c = "c";
	for (j = 0; j < 9; j++) {
		res = c.concat(idI).concat(j);
		casilla = document.getElementById(res);
		if (casilla.value == valor) {
			cont++;
		}
	}
	if (cont > 1) {
		for (j = 0; j < 9; j++) {
			res = c.concat(idI).concat(j);
			casilla = document.getElementById(res);
			casilla.style.background = "#FFE0C1";
		}
	} else {
		for (j = 0; j < 9; j++) {
			res = c.concat(idI).concat(j);
			casilla = document.getElementById(res);
			casilla.style.background = "white";
		}
	}
}
function ComprobarColumna(valor, id, idJ) {
	var i;
	cont = 0;
	var casilla, res;
	var c = "c";
	for (i = 0; i < 9; i++) {
		res = c.concat(i).concat(idJ);
		casilla = document.getElementById(res);
		if (casilla.value == valor) {
			cont++;
		}
	}
	if (cont > 1) {
		for (i = 0; i < 9; i++) {
			res = c.concat(i).concat(idJ);
			casilla = document.getElementById(res);
			casilla.style.background = "#FFE0C1";
		}
	} else {
		for (i = 0; i < 9; i++) {
			res = c.concat(i).concat(idJ);
			casilla = document.getElementById(res);
			casilla.style.background = "white";
		}
	}
}
function ComprobarCuadrado(valor, id, idI, idJ) {

}
function EnviarDato(idI, idJ) {
	var mensaje = {
		TYPE : "SUDOKU",
		funcion : "marcar",
		coordI : idI,
		coordJ : idJ,
		matchID : idPartida
	}
	ws.send(JSON.stringify(mensaje));
}
function marcar(coordI, coordJ) {
	var valor = "";
	var i, j, cont = 0;
	var id = "co";
	var res = id.concat(coordI).concat(coordJ);
	var casilla = document.getElementById(res);
	casilla.value = "X";
}
function limpiarTablero() {
	cargarTableroInicial(tableroini);
	var mensaje = {
		TYPE : "SUDOKU",
		funcion : "limpiar",
		matchID : idPartida
	}
	ws.send(JSON.stringify(mensaje));
}
function popUp(boton) {
	var mensaje;
	var opcion; 
	if (boton == "resolver") {
		opcion=confirm("¿Estas seguro de que quieres resolver?");
		if (opcion == true) {
			if(todasCasillasLlenas()){
				var posibleSolucion = recogerTablero();
				mensaje = {
					TYPE : "SUDOKU",
					funcion: "resolver",
					matchID : idPartida,
					tablero : posibleSolucion
				}
				ws.send(JSON.stringify(mensaje));
			}else{
				alert("No has rellenado todas las casillas");
			}
		}
	} else {
		opcion=confirm("¿Estas seguro de que quieres rendirte?");
		if (opcion == true) {

		} else {

		}
	}
}
function todasCasillasLlenas(){
	var i, j, contador = 0;
	var id = "c";
	var casilla;
	for (i = 0; i < 9; i++) {
		for (j = 0; j < 9; j++) {
			var res = id.concat(i).concat(j);
			casilla = document.getElementById(res);
			if (casilla.value == "") {
				return false;
			}
		}
	}
	return true;
}
function recogerTablero(){
	var resultado="";
	var i, j;
	var id = "c";
	var casilla;
	for (i = 0; i < 9; i++) {
		for (j = 0; j < 9; j++) {
			var res = id.concat(i).concat(j);
			casilla = document.getElementById(res);
			resultado= resultado.concat(casilla.value);
		}
	}
	return resultado;
}
