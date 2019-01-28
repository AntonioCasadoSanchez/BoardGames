<%@ page import="edu.uclm.esi.tysweb.laoca.dominio.*, org.json.*" %>    
<%      
  JSONObject respuesta=new JSONObject();
  String visible = "";
  try {    
    String code = request.getParameter("code");
    Usuario usuario = Manager.get().crearPassNueva(code);
    if(usuario==null){
      visible = "display:hidden";  
    }else{
      visible = "visible";      
      session.setAttribute("usuario", usuario);
    }        
  }
  catch (Exception e) {
    respuesta.put("result", "ERROR");
    respuesta.put("mensaje", e.getMessage());
    System.out.println(e.getMessage());
  }
%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
    <title>La Oca</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">
    <!-- Esto enlaza al archivo de estilos, si no se coloca en la misma carpeta que los html, hay que cambiar la ruta en todos los html, ya que hay un único archivo para todos -->
    <link rel="stylesheet" href="estilos.css">
</head>
<body>
    <section id="login" >
        <div class="contenedor reg" id="pass" visibility=<%=visible%>>
            <h1>LA OCA.
                <small class="text-muted">Cambiar contraseña</small>
            </h1>
            <div>
                <div class="form-group">
                    <label class="campo">Contraseña nueva</label> 
                    <input type="password" class="form-control" id="pwdNew1"  required>
                    <label class="campo">Repita contraseña nueva</label> 
                    <input type="password" class="form-control" id="pwdNew2"  required>
                </div>
                <div class="opciones">
                    <button type="button" class="btn btn-primary" onClick="crearNewPass()">Cambiar contraseña</button>
          <span id="mensajechangePass"></span>
                </div>
            </div>
        </div>
    </section>
    <span id="msgNoTienesPermiso"></span>
    <script src="scripts/GestionCuentas.js"></script>
    <script>
      window.onload=onLoad;
        
        function onLoad(){          
        }
    </script>

</body>
</html>