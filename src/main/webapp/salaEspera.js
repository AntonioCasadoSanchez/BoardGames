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
		var data = message.data;
		data = JSON.parse(data);
		if(data.TYPE=="CHAT"){
			  muestra(data);
		  }
	}
};
		
		/**if (mensaje.tipo=="mensajeChat"){
		message=JSON.parse(message.data);
		var tipo=message.tipo;
		var remitente=message.remitente;
		var contenido=message.contenido;	
		
		/*if (mensaje.tipo=="mensajeChat"){
			var mensajeChat=document.getElementById("chat");
			var jugador=mensaje.nombreJugador;
			var mensajeMostrar=mensaje.mensajeChat;
			mensajeChat.value+="CHAT: "+jugador+": "+mensajeMostrar+".\n\n";
			document.getElementById("txtChat").value="";
			mensajeChat.scrollTop = mensajeChat.scrollHeight;
		}**/
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
		alert("Has llegado aqui sin autenticarte, por favor, no seas tramposo.");
		loadPage("error.html");
	}
};

function enviarChat() {
	var cajaMensaje=document.getElementById("txtChat");
	var user=sessionStorage.userName;
	var texto=cajaMensaje.value;
	if(texto.length==0){
		return;
	}
	var mensaje={
			TYPE : "MENSAJE",
			remitente : user,
			contenido: texto
	}
	ws.send(JSON.stringify(mensaje));
	cajaMensaje.value="";
	/*alert("se envia");
	if(document.getElementById("txtChat").value!=""){
		var p = {
			tipo : "mensajeChat",
			nombreJugador: document.getElementById("usuario").innerHTML,
			mensajeUsuario : document.getElementById("txtChat").value
			};
			ws.send(JSON.stringify(p));
		}*/
};

function muestra(datos){
	var areaMensajes=document.getElementById("chat");
	var usuario = "Yo";
	var msgMostrado = areaMensajes.innerHTML;//guarda en msgmostrado lo que ya habia en el recuadro
	if (datos.remitente == sessionStorage.userName) {
		msgMostrado = msgMostrado + "\n" + usuario + ": " + datos.contenido //añade a msgmostrado el ultimo mensaje
	} else {
		msgMostrado = msgMostrado + "\n" + datos.remitente + ": " + datos.contenido //añade a msgmostrado el ultimo mensaje
	}
	areaMensajes.innerHTML = msgMostrado;//actualiza el textarea con el contenido de msgmostrado.
};

