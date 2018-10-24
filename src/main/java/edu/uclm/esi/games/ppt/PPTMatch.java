package edu.uclm.esi.games.ppt;

import java.util.Random;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.games.Result;
import edu.uclm.esi.games.tictactoe.TictactoeBoard;
import edu.uclm.esi.mongolabels.dao.MongoBroker;

public class PPTMatch extends Match {

	
	public PPTMatch() {
		super();//Llama al constructor de su superclase (clase padre Match)
		this.board= new PPTBoard(this); //con el this, la partida se pasa a si misma al tablero
	}
	@Override
	public void calculateFirstPlayer() {

	}
	public boolean tieneElTurno(Player player) {
		return true;
	}
	@Override
	protected void save() throws Exception{
		Result result = new Result(this.getPlayers().get(0).getUserName(), this.getPlayers().get(1).getUserName(),this.winner.getUserName());
		MongoBroker.get().insert(result);
	}
}
