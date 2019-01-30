var token = "";
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
		} else if(data.TYPE=="PASS"){
			alert("Password actualizada correctamente");
			loadPage("index.html");
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
	//var code = getParameterByName('code');
	//var mail = getParameterByName('email');
	//alert(mail);
	//document.getElementById("imgFoto").src = "data:image/jpeg;base64," + data.foto;

}
function loadPage(url) {
	window.location.assign(url);
};
function controlSeguridad() {
	//Hay que comprobar unas cosas: La primera, que no encontremos nada buscando en la url(significa que getparameters
	//devuelve una cadena vacia y que no venimos de un mail de confirmacion.
	//Si es así pasamos a la siguiente comprobacion, que seria comprobar si venimos de login. (esto ya estaría hecho)
	//Habría que comprobar tambien si tenemos imagen para mostrarla o no
	//Y en el caso de que si qeu encuentre algo en la url, desactivar la parte de volver atrás y la parte de cambiar imagen, 
	//solo dejar la parte para cambiar la contraseña.
	var code = getParameterByName('code');
	if(code != ""){
		document.getElementById("cambioFotoContent").style.display = "none";
		document.getElementById("cerrarsesion").style.display = "none";
		document.getElementById("oldPass").style.display = "none";
		token = true;
		
	}else if(sessionStorage.userName == null){//Aqui tambien hay que hacer la comprobacion
		alert("Has llegado aqui sin autenticarte, por favor, inicia sesion.");
		loadPage("index.html");
	}else{
		token = false;
	}
	
};

function loadFoto(data){
	sessionStorage.foto = data.foto;
	//fotoUsuario.src="data:image/jpg;base64," + data.foto; //si admitimos otros tipos de archivos habria que
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
	function getParameterByName(name) {
	    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
	    results = regex.exec(location.search);
	    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	}
	
	function actualizarPwd(){
		var old = document.getElementById("oldPass").value;
		var tokenizer = getParameterByName('code');
		var nueva1 = document.getElementById("newPass").value;
		var nueva2 = document.getElementById("reNewPass").value;
		if(nueva1 == nueva2){
			if(token){
				var mensaje={
						TYPE : "PASSWORDTOKEN",
						nueva: nueva1,
						token: tokenizer
				}
				ws.send(JSON.stringify(mensaje));
			}else{
				var mensaje={
						TYPE : "PASSWORD",
						vieja : old,
						nueva: nueva1
				}
				ws.send(JSON.stringify(mensaje));
			}
		}else{
			alert("Las contraseñas no coinciden");
		}
	}
