var ws;
	function hola(){
		alert("holaad");
	};
	function login(userName, pwd){
		var request= new XMLHttpRequest();
		request.open("GET", "http://localhost:8080/login?userName=" + userName + "&pwd=" + pwd);
		request.onreadystatechange = function(){
			if(request.readyState==4) {
				textosRecibidos.innerHTML=request.responseText;
				ws = new WebSocket("ws://localhost:8080/gamews");
				ws.onopen= function(){
					add("Conectado");
				}
				ws.onerror = function(){
					add("Error al conectar el WS");
				}
				ws.onmessage = function(message) {//wtf
					var data = message.data;//forma cadena
					data= JSON.parse(data)//parseo a json
					//del c/s o del s/c siempre le mandamos un type
					if(data.TYPE=="MATCH"){
						add(JSON.stringify(data));
					}else
						add("Mensaje Desconocido");
				}
			}
		};
		request.send();
	}
	
	function add(texto){
		var div=document.createElement("div");
		textosRecibidos.appendChild(div);
		div.innerHTML=texto;
	}
	
	function joinGame(gameName){
		var request= new XMLHttpRequest();
		request.open("GET", "http://localhost:8080/joinGame?gameName="+gameName);
	    request.onreadystatechange = function(){
	      if(request.readyState==4) {
	        textosRecibidos.innerHTML=request.responseText;
	      };
	      request.send(); 
	    }
	}
	function move(coordinate){
		//va el websocket
		var p= {
				TYPE : "MOVEMENT",
				coordinates : [ coordinate ]
		};
		//stringificamos
		ws.send(JSON.stringify(p));
}