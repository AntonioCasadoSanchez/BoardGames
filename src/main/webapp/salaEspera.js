function inicio() {
	alert(sessionStorage.userName);
	alert(sessionStorage.email);
	ws = new WebSocket("ws://localhost:8080/gamews");
	ws.onopen = function() {
		alert("hehehe!");
	}
	ws.onerror = function() {
		add("Error al conectar WS");
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