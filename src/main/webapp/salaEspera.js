function inicio() {
	controlSeguridad();
	//alert(sessionStorage.userName);//mostrar el contenido de este mensaje en los divs de arriba.
	//alert(sessionStorage.email);
	ws = new WebSocket("ws://localhost:8080/gamews");
	ws.onopen = function() {
		mostrarInfoUsuario();
	}
	ws.onerror = function() {
		alert("Error al conectar WS");
	}
	ws.onmessage = function(message) {
		var mensaje=message.data;
		mensaje=JSON.parse(mensaje);
		alert("recibo cosas!");
		/*if (mensaje.tipo=="mensajeChat"){
			var mensajeChat=document.getElementById("chat");
			var jugador=mensaje.nombreJugador;
			var mensajeMostrar=mensaje.mensajeChat;
			mensajeChat.value+="CHAT: "+jugador+": "+mensajeMostrar+".\n\n";
			document.getElementById("txtChat").value="";
			mensajeChat.scrollTop = mensajeChat.scrollHeight;
		}*/
		/*var mensaje=datos.data;
		mensaje=JSON.parse(mensaje);
		console.log(mensaje);
		if(message.tipo=="mensajeChat"){
			var mensaje=document.getElementById("chat");
            mensaje.value+=message.mensajeUsuario;
		}*/
		//var data = message.data;
		//data = JSON.parse(data);
		//if (data.TYPE == "MATCH") {
		//	add(JSON.stringify(data));
		//	$("#contenido").load("salaEspera.html");
			// window.location.href =
			// "http://webpub.esi.uclm.es/spa";
			// window.location.href = "salaEspera.html";
		//} else
			//add("Mensaje desconocido");
		
	}
};

function mostrarInfoUsuario(){
	document.getElementById("usuario").innerHTML = sessionStorage.userName;
	document.getElementById("mail").innerHTML = sessionStorage.email;
	document.getElementById("puntos").innerHTML = "10 pts";
}
function loadPage(url) {
	window.location.assign(url);
};
function controlSeguridad() {
	if(sessionStorage.userName == null){
		alert("Te he pillao intruso");
		loadPage("error.html");
	}
};
function elegirJuego(){
	var select = document.getElementById("combo1");
	//alert(select.value);
	
	if (select.value == "string:tictactoe") {
		alert("tictactoe");
	}else if (select.value == "string:Piedra, papel, tijera") {
		loadPage("ppt.html");
	}else {
		alert("Error");
	}
};

function enviarChat() {
	if(document.getElementById("txtChat").value!=""){
		var p = {
			tipo : "mensajeChat",
			nombreJugador: document.getElementById("usuario").innerHTML,
			mensajeUsuario : document.getElementById("txtChat").value
			};
			ws.send( JSON.stringify(p));
		}
};

