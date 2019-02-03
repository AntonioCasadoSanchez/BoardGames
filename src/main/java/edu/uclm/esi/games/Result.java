package edu.uclm.esi.games;

import edu.uclm.esi.mongolabels.labels.Bsonable;

public class Result {
	@Bsonable
	private String tipo;
	@Bsonable
	private String userName1;
	@Bsonable
	private String userName2;
	@Bsonable
	private String winner;

	public Result(String tipo, String userName1, String userName2, String winner) {
		this.tipo=tipo;
		this.userName1 = userName1;
		this.userName2 = userName2;
		this.winner = winner;
	}
}
