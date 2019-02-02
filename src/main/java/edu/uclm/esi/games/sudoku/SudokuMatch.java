package edu.uclm.esi.games.sudoku;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.games.Result;
import edu.uclm.esi.games.ppt.PPTBoard;
import edu.uclm.esi.mongolabels.dao.MongoBroker;

public class SudokuMatch extends Match {

	
	public SudokuMatch() {
		super();//Llama al constructor de su superclase (clase padre Match)
		this.board= new SudokuBoard(this); //con el this, la partida se pasa a si misma al tablero
	}
	@Override
	public void calculateFirstPlayer() {

	}
	public boolean tieneElTurno(Player player) {
		return true;
	}
	@Override
	public void save() throws Exception{
		Result result = new Result(this.getPlayers().get(0).getUserName(), this.getPlayers().get(1).getUserName(),this.winner.getUserName());
		MongoBroker.get().insert(result);
	}
}
