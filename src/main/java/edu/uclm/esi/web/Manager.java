package edu.uclm.esi.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;

import edu.uclm.esi.games.Game;
import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.games.ppt.PPT;
import edu.uclm.esi.games.sudoku.SudokuGame;
import edu.uclm.esi.games.tictactoe.TictactoeGame;

public class Manager {
	private Hashtable<String, Game> games;
	
	private Manager() {
		games=new Hashtable<>();
		Game tictactoe=new TictactoeGame();
		games.put(tictactoe.getName(), tictactoe);
		Game ppt = new PPT();
		games.put(ppt.getName(),ppt);
		Game sudoku = new SudokuGame();
		games.put(sudoku.getName(),sudoku);
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	public static Manager get() {
		return ManagerHolder.singleton;
	}

	/*******************************/
	/**Metodos de la clase Manager**/
	/**
	 * @throws JSONException 
	 * @throws IOException *****************************/
	public Player marcar(UUID id, Player player) {
		Game game=this.games.get("sudoku");
		return game.marcar(id, player);
	}
	
	public Match joinGame(Player player, String gameName) throws JSONException, IOException {
		//Crea un objeto game de tipo Game y hace que sea igual a uno de los que tiene en la lista
		//lista llamada games. Hace un get de esa lista --> get(sudoku) o get(ppt) y le da el Game
		//ppt o sudoku del constructor de esta clase.
		Game game=this.games.get(gameName);
		//Como devuelve un match, devuelve uno llamando al metodo getMatch.
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
