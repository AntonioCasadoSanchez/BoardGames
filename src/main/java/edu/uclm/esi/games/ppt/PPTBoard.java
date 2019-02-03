package edu.uclm.esi.games.ppt;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;

import edu.uclm.esi.games.Board;
import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.web.Manager;
import edu.uclm.esi.web.ws.WSServer;

public class PPTBoard extends Board {

	private final static int PIEDRA = 0;
	private final static int PAPEL = 1;
	private final static int TIJERA = 2;
	private int[] tiradas0, tiradas1;
	private int puntos0, puntos1;

	public PPTBoard(PPTMatch pptmatch) {
		super(pptmatch); // Llamamos al constructor de la madre y ella ya lae asigna el campo.
		// TODO Auto-generated constructor stub
		this.tiradas0 = new int[] { -1, -1, -1 };
		this.tiradas1 = new int[] { -1, -1, -1 };
		this.puntos0 = 0;
		this.puntos1 = 0;
	}

	@Override
	public void move(Player player, int n) throws Exception {
		int pos;
		if (this.match.getPlayers().get(0) == player) { // si es la misma referencia que player hago una cosa sino la
														// otra.
			pos = rellenar(tiradas0, n);
			if (tiradas1[pos] != -1) {
				resolverJugada(pos);
			} else {
				// MandarMensajeAlWSSERVER para uqe comunique al cliente que esta esperando al
				// otro jugador
				WSServer.esperarTirada(this.match.getPlayers().get(0));
			}
		} else {
			pos = rellenar(tiradas1, n);
			if (tiradas0[pos] != -1) {
				resolverJugada(pos);
			} else {
				// MandarMensajeAlWSSERVER para uqe comunique al cliente que esta esperando al
				// otro jugador
				WSServer.esperarTirada(this.match.getPlayers().get(1));
			}
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

	private void resolverJugada(int pos) throws JSONException, IOException {
		int j0 = tiradas0[pos];
		int j1 = tiradas1[pos];
		switch (j0) {
		case 0:
			if(j1== 0) {
				//Piedra vs Piedra, Empate
				WSServer.finJugada(this.match.getPlayers().get(0), this.match.getPlayers().get(1), "Piedra");
			}else if(j1==1) {
				//Piedra vs Papel, gana j1
				WSServer.finJugada(this.match.getPlayers().get(1), this.match.getPlayers().get(0), "Papel", "Piedra");
				puntos1++;
			}else {
				//Piedra vs Tijera, gana j0
				WSServer.finJugada(this.match.getPlayers().get(0), this.match.getPlayers().get(1), "Piedra", "Tijera");
				puntos0++;
			}
			break;
		case 1:
			if(j1== 0) {
				//Papel vs Piedra, gana j0
				WSServer.finJugada(this.match.getPlayers().get(0), this.match.getPlayers().get(1), "Papel", "Piedra");
				puntos0++;
			}else if(j1==1) {
				//Papel vs Papel, Empate
				WSServer.finJugada(this.match.getPlayers().get(0), this.match.getPlayers().get(1), "Papel");
			}else {
				//Papel vs Tijera, gana j1
				WSServer.finJugada(this.match.getPlayers().get(1), this.match.getPlayers().get(0), "Tijera", "Papel");
				puntos1++;
			}
			break;
		case 2:
			if(j1== 0) {
				//Tijera vs Piedra, gana j1
				WSServer.finJugada(this.match.getPlayers().get(1), this.match.getPlayers().get(0), "Piedra", "Tijera");
				puntos1++;
			}else if(j1==1) {
				//Tijera vs Papel, gana j0
				WSServer.finJugada(this.match.getPlayers().get(0), this.match.getPlayers().get(1), "Tijera", "Papel");
				puntos0++;
			}else {
				//Tijera vs Tijera, Empate
				WSServer.finJugada(this.match.getPlayers().get(0), this.match.getPlayers().get(1), "Tijera");
			}
			break;
		}
	}

	public Player getWinner() {
		for (int i = 0; i < tiradas0.length; i++)
			if (tiradas0[i] == -1 || tiradas1[i] == -1)
				return null;

		return gana(tiradas0, tiradas1);

	}
	// }

	private Player gana(int[] a, int[] b) {
		int victoriasA = 0, victoriasB = 0;
		for (int i = 0; i < a.length; i++) {
			if (gana(a[i], b[i]))
				victoriasA++;
			else
				victoriasB++;
		}
		return victoriasA > victoriasB ? this.match.getPlayers().get(0) : this.match.getPlayers().get(1);
	}

	private boolean gana(int a, int b) {// INCOMPLETO VER TODOS LOS CASOS
		if (a == PIEDRA && b == TIJERA) {
			return true;
		}
		if (a == PAPEL && b == PIEDRA) {
			return true;
		}
		if (a == TIJERA && b == PAPEL) {
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

	public boolean end() {
		if (this.getWinner() != null)
			return true;
		for (int i = 0; i < tiradas0.length; i++)
			if (tiradas0[i] == -1 || tiradas1[i] == 1)
				return false;
		return true;
	}

	@Override
	public void end(Player player, UUID id) throws Exception {
		Manager.get().end("Piedra, papel, tijera", player,id);

	}

	@Override
	public void fin() throws Exception {
		if(puntos0 > puntos1) {
			WSServer.finPPT("Win", this.match.getPlayers().get(0), this.match.getPlayers().get(1), this.match.getId());
		}else if (puntos1 > puntos0) {
			WSServer.finPPT("Win", this.match.getPlayers().get(1), this.match.getPlayers().get(0), this.match.getId());
		}else {
			WSServer.finPPT("Tie", this.match.getPlayers().get(0), this.match.getPlayers().get(1), this.match.getId());
		}
	}

}
