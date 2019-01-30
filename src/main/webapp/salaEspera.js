function inicio() {
	controlSeguridad();
	//loadGames();
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
		  }else if (data.TYPE=="FOTO"){
			  loadFoto(data);
		  }
	}
	ws.onclose = function(){
		alert("adios");
	}
};

function loadGames() {
	$.get("/games", function(respuesta, estado) {
		if (estado=="success") {
			for (var i=0; i<respuesta.length; i++) {
				var option=document.createElement("option");
				option.setAttribute("value", respuesta[i]);
				option.innerHTML=respuesta[i];
				$("#selectGames").append(option);
				
			}
		}
	});
}
function joinGame(b) {
	alert(b);
	loadPage
	
	alert(b);
	var gameName;
	if (b == "boton_ppt") {
		gameName="tictactoe";
	}else if (b == "boton_destape") {
		gameName="Destape";
	}else {
		alert("error");
	}
	
	$.post("joinGame", gameName, function(respuesta, estado) {
		if (estado=="success") {
			alert("hola");
		}
	});
}
		
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
		

function closeSession(){
	alert("Has cerrado sesion");
}
function mostrarInfoUsuario(){
	document.getElementById("usuario").innerHTML = sessionStorage.userName;
	document.getElementById("mail").innerHTML = sessionStorage.email;
	document.getElementById("puntos").innerHTML = "10 pts";
	
	//document.getElementById("imgFoto").src = "data:image/jpeg;base64," + data.foto;

}
function loadPage(url) {
	window.location.assign(url);
};
function controlSeguridad() {	
	var req= new XMLHttpRequest();
	req.open("POST", "controlSeguridad");
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.onreadystatechange = function(response){
		if(req.readyState==4){
			if(req.status==200){
				if(!response.returnValue){
					alert("Has llegado aqui sin autenticarte, por favor, inicia sesion.");
					loadPage("index.html");
				}
			}else{
				alert("problema con la peticion http");
			}
		}
	};
	req.send();
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
function loadFoto(data){
	
	imgFoto.src="data:image/jpg;base64," + data.foto; 
	logo = document.getElementById("imgFoto");
	sessionStorage.foto = logo;
	//si admitimos otros tipos de archivos habria que
	//controlarlo, y luego dependiendo del tipo(añadido en el wserver donde ponermos el type foto) aqui se pone el image/jpeg
	//o image/loquesea.
}
function previewFile() {
	  var preview = document.querySelector('img');
	  var file    = document.querySelector('input[type=file]').files[0];
	  var reader  = new FileReader();

	  reader.onloadend = function () {
	    preview.src = reader.result;
	  }

	  if (file) {
	    reader.readAsDataURL(file);
	  } else {
	    preview.src = "";
	  }
	}

	function subirFoto(){
		var file=fotofile.files[0];
		var reader=new FileReader();
		
		reader.onload=function(e){
			var blob = new Blob([reader.result, 'P']);
			var blobReader = new FileReader();
			blobReader.onload = function(event) {
				var buffer = event.target.result;
				ws.send(buffer);
			};
			blobReader.readAsArrayBuffer(blob);
		}
		reader.readAsArrayBuffer(file);
	}
