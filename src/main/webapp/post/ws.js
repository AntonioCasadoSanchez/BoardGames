
var url=window.location;
var recurso="ws://localhost:8080/gamews";
var ws=null;

function conectar() {
	ws=new WebSocket(recurso);
	ws.onopen = function() {
		spanEstado.innerHTML="Connected";
	}
	
	ws.onmessage = function(mensaje) {
		var div=document.createElement("div");
		divMensajes.appendChild(div);
		div.innerHTML=mensaje.data;
	}
	
	ws.onerror = function(mensaje) {
		spanEstado.innerHTML="Desconectado";
		spanEstado.setAttribute("style", "color:red");
	}
	
	ws.onclose = function(mensaje) {
		spanEstado.innerHTML="Desconectado";
		spanEstado.setAttribute("style", "color:red");
	}
}
