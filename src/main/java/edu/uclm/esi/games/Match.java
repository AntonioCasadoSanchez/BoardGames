package edu.uclm.esi.games;

import java.util.UUID;
import java.util.Vector;

public abstract class Match {
	protected UUID id;
	protected Vector<Player> players;
	protected int currentPlayer;
	protected Player winner;
	protected Board board;
	
	public Match() {
		this.id=UUID.randomUUID();
		this.players=new Vector<>();
		this.currentPlayer=-1;
	}
	
	public UUID getId() {
		return id;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
		player.setCurrentMatch(this);
	}
	
	public Vector<Player> getPlayers() {
		return players;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Player getWinner() {
		return winner;
	}
	public void setWinner(Player player) {
		this.winner=player;
	}
	
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public Match move(Player player, int n) throws Exception {
		this.board.move(player, n);
		return this;
	}

	public abstract void save() throws Exception;
		//MongoBroker, toma un objeto y lo guarda en la BD vamos a crear una clase enfrentamiento y vamos a almacenar esos objetos en la BD
	public abstract void calculateFirstPlayer();
	public abstract boolean tieneElTurno(Player player);

	public void fin() throws Exception {
		this.board.fin();		
	}
	}
