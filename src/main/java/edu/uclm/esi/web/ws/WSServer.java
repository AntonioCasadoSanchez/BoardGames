package edu.uclm.esi.web.ws;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.mongolabels.dao.MongoBroker;
import edu.uclm.esi.web.Manager;

@Component
public class WSServer extends TextWebSocketHandler {
	private static ConcurrentHashMap<String, WebSocketSession> sessionsById=new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, WebSocketSession> sessionsByPlayer=new ConcurrentHashMap<>();
	
	/**OnOpen**/
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionsById.put(session.getId(), session);
		Player player = (Player) session.getAttributes().get("player");
		String userName=player.getUserName();
		if (sessionsByPlayer.get(userName)!=null) {
			sessionsByPlayer.remove(userName);
		}
		sessionsByPlayer.put(userName, session);
	}
	
	/**OnMessage**/
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		JSONObject jso=new JSONObject(message.getPayload());
		
		/**Si el mensaje es del Chat**/
		if(jso.getString("TYPE").equals("MENSAJE")) {
			sendChat(session, jso);
		}
	}
	
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		Player player = (Player) session.getAttributes().get("player");
		byte[] bytes= message.getPayload().array();
		player.setFoto(bytes);
		BsonDocument criterion=new BsonDocument();
		criterion.append("userName", new BsonString(player.getUserName()));
		MongoBroker.get().delete("Player",criterion);
		try {
			MongoBroker.get().insert(player);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**OnClose**/
	@Override
	public void afterConnectionClosed (WebSocketSession session, CloseStatus status) throws Exception {
		//Deberemos comprobar si la session estaba en partida, y si es asi dar la sesion por finalizada
	}
	
	/*******************************************************/
	/**Metodos que son llamados desde la parte de Override**/
	/*******************************************************/
	
	public static void sendChat(WebSocketSession session, JSONObject jso ) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			obj.put("TYPE",	"CHAT");
			obj.put("remitente", jso.getString("remitente"));
			obj.put("contenido", jso.getString("contenido"));
			
			WebSocketMessage<?> message= new TextMessage(obj.toString());
			for(Entry<String, WebSocketSession> entry : sessionsByPlayer.entrySet()) {
				entry.getValue().sendMessage(message);
			}
		}catch (Exception e) {
			e.printStackTrace();
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
	}
}
