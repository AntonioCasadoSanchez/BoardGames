package edu.uclm.esi.games.ppt;

import edu.uclm.esi.games.Game;
import edu.uclm.esi.games.Match;

public class PPT extends Game {
	
	

	public PPT() {
		super(2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Piedra, papel, tijera";
	}

	@Override
	protected Match createMatch() {
		return new PPTMatch();
	}

}
