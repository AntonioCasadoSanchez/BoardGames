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
		alert("recibo cosas!");
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

