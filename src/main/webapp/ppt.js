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
		else if (data.TYPE == "WAIT_SIGUIENTE_TIRADA") {
			empezarPartida(data);
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
	var player1 = document.getElementById("cej1");
	var player2 = document.getElementById("cej2");
	player1.value = data.Player1;
	player2.value = data.Player2;
	if (player1.value != "" && player2.value != "") {
		timerOn();
	}
}
var totalTiempo=5;

function timerOn() {
	var contador = document.getElementById("timer");
	 var btnSiguiente;
	 if(totalTiempo==0)
     {
		 document.getElementById('timer').innerHTML = "Se acabó el tiempo"; 
		 btnSiguiente = document.getElementById("btnSiguienteRonda");
		 btnSiguiente.style.display = 'inline';
     }else{
         /* Restamos un segundo al tiempo restante */
    	 contador.innerHTML = totalTiempo;
         totalTiempo-=1;
         /* Ejecutamos nuevamente la función al pasar 1000 milisegundos (1 segundo) */
         setTimeout("timerOn()",1000);
     }
}
function siguienteTirada() {
alert("hola");
}
