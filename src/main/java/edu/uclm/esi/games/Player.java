package edu.uclm.esi.games;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uclm.esi.mongolabels.dao.MongoBroker;
import edu.uclm.esi.mongolabels.dao.Object2Bson;
import edu.uclm.esi.mongolabels.labels.Bsonable;

public class Player {
	@Bsonable
	private String userName;
	@Bsonable
	private String email;
	@Bsonable
	private String pwd;
	@JsonIgnore
	private Match currentMatch;
	@Bsonable
	private byte[] foto;
	@Bsonable
	private String type; // Si es de google, se le pone google, y si no, se le pone normal
	@Bsonable
	private String idGoogle;

	/** Getters y Setters **/
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private void setIdGoogle(String idGoogle2) {
		this.idGoogle = idGoogle2;
		this.type="Google";
	}

	public String getEmail() {
		return email;
	}

	public void setFoto(byte[] bytes) {
		this.foto = bytes;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setCurrentMatch(Match match) {
		this.currentMatch = match;
	}

	public Match getCurrentMatch() {
		return currentMatch;
	}

	/********************************/
	/** Metodos de la clase Player **/
	/********************************/

	public static Player identify(String userName, String pwd) throws Exception {
		BsonDocument criterion = new BsonDocument();
		criterion.append("userName", new BsonString(userName)).put("pwd", new BsonString(pwd));
		Player player = (Player) MongoBroker.get().loadOne(Player.class, criterion);
		return player;
	}

	public static Player register(String email, String userName, String pwd) throws Exception {
		Player player = new Player();// Dara error?
		player.setEmail(email);
		player.setUserName(userName);
		player.setPwd(pwd);
		MongoBroker.get().insert(player);
		return player;
	}

	public Match move(int[] coordinates) throws Exception {
		return this.currentMatch.move(this, coordinates);
	}

	public static Player identifyGoogle(String idGoogle, String nombre, String email) throws Exception {
		BsonDocument criterion = new BsonDocument();
		criterion.append("idGoogle", new BsonString(idGoogle)).put("userName", new BsonString(nombre));//en azul los campos que tengas en la BD?
		criterion.append("email", new BsonString(email));
		criterion.append("type", new BsonString("Google"));
		Player player = (Player) MongoBroker.get().loadOne(Player.class, criterion);
		return player;
	}

	public static Player registerGoogle(String idGoogle, String nombre, String email) throws Exception {
		Player player = new Player();
		player.setEmail(email);
		player.setUserName(nombre);
		player.setIdGoogle(idGoogle);
		MongoBroker.get().insert(player);
		return player;

	}

	public static Player solicitarToken(String userName) {
		Player player = null;
		try {
			BsonDocument criterion = new BsonDocument();
			criterion.append("userName", new BsonString(userName));
			player = (Player) MongoBroker.get().loadOne(Player.class, criterion);
			player.createToken();
		} catch (Exception e) {

		}
		return player;
	}

	private void createToken() throws Exception {
		Token token = new Token(this.userName);
		MongoBroker.get().insert(token);
		EMailSenderService email = new EMailSenderService();
		email.enviarPorGmail(this.email, token.getValor());
	}

	public byte[] loadFoto() {
		//Hay que ir al mongo, buscar la tabla Fotos, la columna bytes, de este userName.
		try {
			BsonDocument criterion = new BsonDocument();
			criterion.append("userName",  new BsonString(this.userName));
			//el criterio es que en el campo user name sea este user name.
			BsonDocument result= MongoBroker.get().loadBinary("Fotos", criterion);
			return result.getBinary("bytes").getData();
		}catch(Exception e){
			return null;
		}
	}

	public boolean changePass(JSONObject jso) throws JSONException {
		boolean resultado = false;
		String old = jso.getString("vieja");
		String nueva = jso.getString("nueva");
		BsonDocument criterion = new BsonDocument();
		criterion.append("userName",  new BsonString(this.userName));
		Player playera = null;
		if(old.equals(this.pwd)) {
			try {
					playera = (Player) MongoBroker.get().loadOne(Player.class, criterion);
					MongoBroker.get().delete("Player", criterion);
					playera.setPwd(nueva);
					BsonDocument bsoPlayer = Object2Bson.getBsonDocument(playera);
					MongoBroker.get().insertBson(Player.class, bsoPlayer);
					resultado= true;
			}catch(Exception e) {
				System.out.println("Ha habido algun problema cambiando la password");
				return resultado;
			}
		}
		return resultado;
		
	}

	public static boolean actualizarPass(JSONObject jso) throws Exception {
		boolean resultado = false;
		String nueva = jso.getString("nueva");
		String token = jso.getString("token");
		BsonDocument criterion = new BsonDocument();
		criterion.append("valor",  new BsonString(token));
		Player playera = null;
		Token toki = null;
			toki = (Token) MongoBroker.get().loadOne(Token.class, criterion);
			if(toki.getCaducidad() < System.currentTimeMillis()) {
				return resultado;
			}
			BsonDocument criterion2 = new BsonDocument();
			criterion2.append("userName", new BsonString(toki.getUserName()));
			playera = (Player) MongoBroker.get().loadOne(Player.class, criterion2);
			MongoBroker.get().delete("Player", criterion2);
			playera.setPwd(nueva);
			MongoBroker.get().insert(playera);
			resultado = true;
		return resultado;
	}
}
