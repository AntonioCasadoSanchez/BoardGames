package edu.uclm.esi.games;

import org.bson.BsonDocument;
import org.bson.BsonString;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.esi.mongolabels.dao.MongoBroker;
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
	private String type; //Si es de google, se le pone google, y si no, se le pone normal
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
		this.idGoogle=idGoogle2;
		
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setFoto(byte[] bytes) {
		this.foto=bytes;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	private void setPwd(String pwd) {
		this.pwd=pwd;
	}
	
	public void setCurrentMatch(Match match) {
		this.currentMatch=match;
	}
	
	public Match getCurrentMatch() {
		return currentMatch;
	}
	/********************************/
	/** Metodos de la clase Player **/
	/********************************/
	
	public static Player identify(String userName, String pwd) throws Exception {
		BsonDocument criterion=new BsonDocument();
		criterion.append("userName", new BsonString(userName)).put("pwd", new BsonString(pwd));
		Player player=(Player) MongoBroker.get().loadOne(Player.class, criterion);
		return player;
	}

	public static Player register(String email, String userName, String pwd) throws Exception {
		Player player=new Player();//Dara error?
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
		BsonDocument criterion=new BsonDocument();
		criterion.append("idGoogle", new BsonString(idGoogle)).put("nombre", new BsonString(nombre));
		criterion.append("email", new BsonString(email));
		criterion.append("tipo",  new BsonString("Google"));
		Player player= (Player) MongoBroker.get().loadOne(Player.class, criterion);
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
	
	private void createToken() throws Exception{
		Token token= new Token(this.userName);
	}
}
