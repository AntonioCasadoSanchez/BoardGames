function inicio() {
	controlSeguridad();
	//loadGames();
	ws = new WebSocket("ws://localhost:8080/gamews");
	ws.onopen = function() {
		alert(sessionStorage.userName);
	}
	ws.onerror = function() {
		alert("Error al conectar WS");
	}
	ws.onmessage = function(message) {
		/**var data = message.data;
		data = JSON.parse(data);
		if(data.TYPE=="CHAT"){
			  muestra(data);
		  }else if (data.TYPE=="FOTO"){
			  loadFoto(data);
		  }
	}
	ws.onclose = function(){
		alert("adios");
	}**/
};