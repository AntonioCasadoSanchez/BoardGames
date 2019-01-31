package edu.uclm.esi.games.sudoku;

import edu.uclm.esi.games.Game;
import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.ppt.PPTMatch;

public class SudokuGame extends Game {
	
	public SudokuGame() {
		super(2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "sudoku";
	}

	@Override
	protected Match createMatch() {
		return new SudokuMatch();
	}

}
