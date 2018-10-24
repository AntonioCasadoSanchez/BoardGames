package edu.uclm.esi.games.ppt;

import edu.uclm.esi.games.Board;
import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;

public class PPTBoard extends Board {

	private final static int PIEDRA = 0;
	private final static int PAPEL = 1;
	private final static int TIJERA = 2;
	private int[] tiradas0, tiradas1;

	public PPTBoard(PPTMatch pptmatch) {
		super(pptmatch); // Llamamos al constructor de la madre y ella ya lae asigna el campo.
		// TODO Auto-generated constructor stub
		this.tiradas0 = new int[] { -1, -1, -1 };
		this.tiradas1 = new int[] { -1, -1, -1 };
	}

	@Override
	public void move(Player player, int[] coordinates) throws Exception {
		int pos;
		if (this.match.getPlayers().get(0) == player) { // si es la misma referencia que player hago una cosa sino la
														// otra.
			pos = rellenar(tiradas0, coordinates[0]);
		} else {
			pos = rellenar(tiradas1, coordinates[0]);
		}
	}

	private int rellenar(int[] tiradas, int valor) {
		for (int i = 0; i < tiradas.length; i++) {
			if (tiradas[i] == -1) {
				tiradas[i] = valor;
				return i;
			}
		}
		return -1;

	}

	@Override
	public Player getWinner() {
			for (int i = 0; i < tiradas0.length; i++) 
				if (tiradas0[i] == -1 || tiradas1[i] == -1) 
					return null;
			
			return gana(tiradas0,tiradas1);

	}
//		}


	private Player gana(int[] a, int[] b) {
		int victoriasA=0, victoriasB=0;
		for(int i=0; i<a.length;i++) {
			if(gana(a[i],b[i]))
				victoriasA++;
			else
				victoriasB++;
		}
		return victoriasA>victoriasB ? this.match.getPlayers().get(0) : this.match.getPlayers().get(1);
	}
	private boolean gana(int a, int b) {//INCOMPLETO VER TODOS LOS CASOS
		if (a==PIEDRA && b == TIJERA) {
			return true;
		}
		if (a==PAPEL && b == PIEDRA) {
			return true;
		}
		if (a==TIJERA && b == PAPEL) {
			return true;	
		}
		return false;
	}

	public int[] getTiradas0() {
		return tiradas0;
	}

	public int[] getTiradas1() {
		return tiradas1;
	}

	@Override
	public boolean end() {
		if(this.getWinner()!=null)
			return true;
		for(int i=0; i<tiradas0.length;i++)
			if(tiradas0[i]== -1 || tiradas1[i]==1)
				return false;
		return true;
	}

}
