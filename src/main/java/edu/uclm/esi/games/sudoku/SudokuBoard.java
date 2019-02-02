package edu.uclm.esi.games.sudoku;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;

import edu.uclm.esi.games.Board;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.games.ppt.PPTMatch;
import edu.uclm.esi.web.Manager;

public class SudokuBoard extends Board {
	
	private final static String BoardSolucion ="762184935149235678358796124935428716871659342624317859483561297297843561516972483";
	private final static String BoardInicial = "060000030009000008008700104000400006801600300004307050400001007097000060500000400";

	public SudokuBoard(SudokuMatch sudomatch) {
		super(sudomatch); // Llamamos al constructor de la madre y ella ya lae asigna el campo.
	}

	@Override
	public void move(Player player, int[] coordinates) throws Exception {
		int pos;
		if (this.match.getPlayers().get(0) == player) { // si es la misma referencia que player hago una cosa sino la
														// otra.
			//pos = rellenar(tiradas0, coordinates[0]);
		} else {
			//pos = rellenar(tiradas1, coordinates[0]);
		}
	}
	public static String cargarTableroSolucion() {
		return BoardSolucion;
	}
	public static String cargarTableroInicial() {
		return BoardInicial;
	}

	@Override
	public void end(Player player, UUID id) throws JSONException, IOException {
		Manager.get().end(player,id);
	}

	@Override
	public boolean end() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
}