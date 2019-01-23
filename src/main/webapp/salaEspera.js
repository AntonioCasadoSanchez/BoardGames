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
			  muestra(JSON.stringify(data));
		  }
	}
};
		
		/**if (mensaje.tipo=="mensajeChat"){
		message=JSON.parse(message.data);
		var tipo=message.tipo;
		var remitente=message.remitente;
		var contenido=message.contenido;
		
		var texto=areaMensajes.innerHTML; //coge en texto lo que ya habia en el recuadro
		texto=texto+"\n" + remitente + ": " + contenido; //añade a texto el ultimo mensaje
		areaMensajes.innerHTML=texto; //actualiza el textarea con el contenido de texto.
		
		
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
	var areaMensajes=document.getElementById("chat");
	var cajaMensaje=document.getElementById("txtChat");
	var email=sessionStorage.email;
	var texto=cajaMensaje.value;
	if(texto.length==0){
		return;
	}
	var mensaje={
			TYPE : "MENSAJE",
			remitente : email,
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
	areaMensajes.innerHTML = datos;
};

