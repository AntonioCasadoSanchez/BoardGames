package edu.uclm.esi.games.tictactoe;

import edu.uclm.esi.games.Game;
import edu.uclm.esi.games.Match;

public class TictactoeGame extends Game {

	public TictactoeGame() {
		super(2);
	}

	@Override
	public String getName() {
		return "tictactoe";
	}

	@Override
	protected Match createMatch() {
		return new TictactoeMatch();
	}

}
