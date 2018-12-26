package edu.uclm.esi.web.ws;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.web.Manager;

@Component
public class WSServer extends TextWebSocketHandler {
	private static ConcurrentHashMap<String, WebSocketSession> sessionsById=new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, WebSocketSession> sessionsByPlayer=new ConcurrentHashMap<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {//SE EJECUTA CUANDO SE ESTABLECE EL HANDSHAKE
		//JOptionPane.showMessageDialog(null, "hola");
		sessionsById.put(session.getId(), session);
		Player player = (Player) session.getAttributes().get("player");
		String userName=player.getUserName();
		if (sessionsByPlayer.get(userName)!=null) {
			sessionsByPlayer.remove(userName);
		}
		sessionsByPlayer.put(userName, session);
	}
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//JOptionPane.showMessageDialog(null, "hola");
		System.out.println(message.getPayload());
		JSONObject jso=new JSONObject(message.getPayload());
		if(jso.getString("TYPE").equals("MOVEMENT")) {
			Player player = (Player) session.getAttributes().get("player");
			JSONArray coordinates = jso.getJSONArray("coordinates");
			Match match = Manager.get().move(player, coordinates);//PREGUNTAR ESTA LINEA. mas o menos ya lo entiendo
			send(match.getPlayers(), match);
			
		}
	}

	public static void send(Vector<Player> players, Match match) {
		ObjectMapper mapper=new ObjectMapper();
		JSONObject jso;
		try {
			//jso=mapper.writeValueAsString(match);//convierte partida a cadena en formato json.
			jso=new JSONObject(mapper.writeValueAsString(match));
			jso.put("TYPE",	"MATCH");
			for(Player player : players) {
				WebSocketSession session=sessionsByPlayer.get(player.getUserName());
				WebSocketMessage<?> message= new TextMessage(jso.toString());
				session.sendMessage(message);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub	
	}
	@OnOpen
	public void onOpen() {
		JOptionPane.showMessageDialog(null, "Conectado");
	}
}
