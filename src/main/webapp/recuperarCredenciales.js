function getUser(){
	var usuario = document.getElementById("usuario").value;
	//window.location.href = "http://localhost:8080/solicitarToken?userName=" + usuario;
	var req= new XMLHttpRequest();
	req.open("POST", "solicitarToken");
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.onreadystatechange = function(response){
		if(req.readyState==4){
			if(req.status==200){
				alert("Email enviado??");
			}else{
				alert("error, el mail no coincide");
			}
		}
	};
	var p="userName=" + usuario;
	req.send(p);

}