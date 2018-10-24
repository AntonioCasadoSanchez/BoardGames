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
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	private void setPwd(String pwd) {
		this.pwd=pwd;
	}

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

	public void setCurrentMatch(Match match) {
		this.currentMatch=match;
	}
	
	public Match getCurrentMatch() {
		return currentMatch;
	}

	public Match move(int[] coordinates) throws Exception {
		return this.currentMatch.move(this, coordinates);
	}
}
