function inicio() {
	controlSeguridad();
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
		if (data.TYPE=="FOTO"){
			  loadFoto(data);
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
		

function closeSession(){
	alert("Has cerrado sesion");
}
function mostrarInfoUsuario(){
	document.getElementById("usuario").innerHTML = sessionStorage.userName;
	document.getElementById("mail").innerHTML = sessionStorage.email;
	document.getElementById("puntos").innerHTML = "20 pts";
	
	//document.getElementById("imgFoto").src = "data:image/jpeg;base64," + data.foto;

}
function loadPage(url) {
	window.location.assign(url);
};
function controlSeguridad() {
	if(sessionStorage.userName == null){
		alert("Has llegado aqui sin autenticarte, por favor, inicia sesion.");
		loadPage("index.html");
	}
};

function loadFoto(data){
	sessionStorage.foto = data.foto;
	fotoUsuario.src="data:image/jpg;base64," + data.foto; //si admitimos otros tipos de archivos habria que
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
