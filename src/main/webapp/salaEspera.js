function inicio() {
	controlSeguridad();
}
function controlSeguridad() {
	var req = new XMLHttpRequest();
	req.open("POST", "controlSeguridad");
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.onreadystatechange = function(response) {
		if (req.readyState == 4) {
			if (req.status == 200) {
				if (req.responseText != "true") {
					alert("Has llegado aqui sin autenticarte, por favor, inicia sesion.");
					loadPage("index.html");
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
		mostrarInfoUsuario();
	}
	ws.onerror = function() {
		alert("Error al conectar WS");
	}
	ws.onmessage = function(message) {
		var data = message.data;
		data = JSON.parse(data);
		if (data.TYPE == "CHAT") {
			muestra(data);
		} else if (data.TYPE == "FOTO") {
			loadFoto(data);
		} else if (data.TYPE == "CERRAR") {
			ws.onclose();
		}
	}
	ws.onclose = function() {
		alert("Conexion cerrada por el servidor");
		window.location.assign("index.html");
	}
};
/*********BORRAR???????****************/
function loadGames() {
	$.get("/games", function(respuesta, estado) {
		if (estado == "success") {
			for (var i = 0; i < respuesta.length; i++) {
				var option = document.createElement("option");
				option.setAttribute("value", respuesta[i]);
				option.innerHTML = respuesta[i];
				$("#selectGames").append(option);

			}
		}
	});
}
/*********BORRAR???????****************/
/*********BORRAR???????****************/
function joinGame(b) {
	alert(b);
	loadPage

	alert(b);
	var gameName;
	if (b == "boton_ppt") {
		gameName = "tictactoe";
	} else if (b == "boton_destape") {
		gameName = "Destape";
	} else {
		alert("error");
	}

	$.post("joinGame", gameName, function(respuesta, estado) {
		if (estado == "success") {
			alert("hola");
		}
	});
}
/*********BORRAR???????****************/
function closeSession() {
	alert("Has cerrado sesion");
	loadPage("index.html");
}
function mostrarInfoUsuario() {
	document.getElementById("usuario").innerHTML = sessionStorage.userName;
	document.getElementById("mail").innerHTML = sessionStorage.email;
	document.getElementById("puntos").innerHTML = "10 pts";
	pedirFoto();
}
function pedirFoto(){
	var mensaje = {
			TYPE : "AVATAR",		
		}
		ws.send(JSON.stringify(mensaje));
}
function loadPage(url) {
	window.location.assign(url);
};

function enviarChat() {
	var cajaMensaje = document.getElementById("txtChat");
	var user = sessionStorage.userName;
	var texto = cajaMensaje.value;
	if (texto.length == 0) {
		return;
	}
	var mensaje = {
		TYPE : "MENSAJE",
		remitente : user,
		contenido : texto
	}
	ws.send(JSON.stringify(mensaje));
	cajaMensaje.value = "";
};

function muestra(datos) {
	var areaMensajes = document.getElementById("chat");
	var usuario = "Yo";
	var msgMostrado = areaMensajes.innerHTML;//guarda en msgmostrado lo que ya habia en el recuadro
	if (datos.remitente == sessionStorage.userName) {
		msgMostrado = msgMostrado + "\n" + usuario + ": " + datos.contenido //añade a msgmostrado el ultimo mensaje
	} else {
		msgMostrado = msgMostrado + "\n" + datos.remitente + ": "
				+ datos.contenido //añade a msgmostrado el ultimo mensaje
	}
	areaMensajes.innerHTML = msgMostrado;//actualiza el textarea con el contenido de msgmostrado.
};
function loadFoto(data) {
	imgFoto.src = "data:image/jpg;base64," + data.foto;
	logo = document.getElementById("imgFoto");
	sessionStorage.foto = logo;
}
function previewFile() {
	var preview = document.querySelector('img');
	var file = document.querySelector('input[type=file]').files[0];
	var reader = new FileReader();

	reader.onloadend = function() {
		preview.src = reader.result;
	}
	if (file) {
		reader.readAsDataURL(file);
	} else {
		preview.src = "";
	}
}
function subirFoto() {
	var file = fotofile.files[0];
	var reader = new FileReader();

	reader.onload = function(e) {
		var blob = new Blob([ reader.result, 'P' ]);
		var blobReader = new FileReader();
		blobReader.onload = function(event) {
			var buffer = event.target.result;
			ws.send(buffer);
		};
		blobReader.readAsArrayBuffer(blob);
	}
	reader.readAsArrayBuffer(file);
}
