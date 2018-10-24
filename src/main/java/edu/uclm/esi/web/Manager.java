package edu.uclm.esi.web;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;

import edu.uclm.esi.games.Game;
import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.games.ppt.PPT;
import edu.uclm.esi.games.tictactoe.TictactoeGame;

public class Manager {
	private Hashtable<String, Game> games;
	
	private Manager() {
		games=new Hashtable<>();
		Game tictactoe=new TictactoeGame();
		games.put(tictactoe.getName(), tictactoe);
		Game ppt = new PPT();
		games.put(ppt.getName(),ppt);
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	public static Manager get() {
		return ManagerHolder.singleton;
	}

	public Match joinGame(Player player, String gameName) {
		Game game=this.games.get(gameName);
		return game.getMatch(player);
	}

	public JSONArray getGames() {
		JSONArray jsa=new JSONArray();
		Enumeration<Game> eGames = games.elements();
		while (eGames.hasMoreElements())
			jsa.put(eGames.nextElement().getName());
		return jsa;
	}

	public Match move(Player player, JSONArray coordinates) throws Exception {
		// TODO Auto-generated method stub
		int[] iC = new int[coordinates.length()];
		for(int i=0; i<iC.length; i++) {
			iC[i]=coordinates.getInt(i);
		}
		return player.move(iC);
	}

}
