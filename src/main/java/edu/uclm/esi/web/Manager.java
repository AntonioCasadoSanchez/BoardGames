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
	public Player devolverOponente(UUID id, Player player) {
		Game game=this.games.get("sudoku");
		return game.devolverOponente(id, player);
	}
	public Match devolverPartido(UUID id) {
		Game game=this.games.get("sudoku");
		return game.devolverPartido(id);
	}
	public void end(String juego, Player player, UUID id) throws JSONException, IOException {
		Game game=this.games.get(juego);
		game.end(juego, player, id);
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
	public void fin(Player player) throws Exception {
		player.fin();		
	}
	public Match move(Player player, int n) throws Exception {
		// TODO Auto-generated method stub
		return player.move(n);
	}

}
