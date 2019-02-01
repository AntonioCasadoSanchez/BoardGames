package edu.uclm.esi.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.web.ws.WSServer;

public abstract class Game {
	protected List<Match> pendingMatches;
	protected ConcurrentHashMap<UUID, Match> inPlayMatches;
	protected int numberOfPlayers;
	
	public Game(int numberOfPlayers) {
		this.pendingMatches=Collections.synchronizedList(new ArrayList<>());
		this.inPlayMatches=new ConcurrentHashMap<>();
		this.numberOfPlayers=numberOfPlayers;
	}

	public abstract String getName();

	public Match getMatch(Player player) throws JSONException, IOException {
		Match match;
		if (this.pendingMatches.size()==0) {
			match=createMatch();
			match.addPlayer(player);
			pendingMatches.add(match);
			WSServer.waitPlayer(player);
		} else {
			match=this.pendingMatches.get(0);
			match.addPlayer(player);
			if (match.getPlayers().size()==this.numberOfPlayers) {
				match=this.pendingMatches.remove(0);
				inPlayMatches.put(match.getId(), match);
				match.calculateFirstPlayer();
				WSServer.inicioPartida(match.getPlayers(), match);
				//WSServer.send(match.getPlayers(), match);
				
			}
		}
		return match;
	}

	protected abstract Match createMatch();

	public Player marcar(UUID id, Player player) {
		Match match= inPlayMatches.get(id);
		Vector<Player> vector = match.getPlayers();
		if(vector.get(0) == player) {
			return vector.get(1);
		}
		return vector.get(0);
	}
}
