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
import org.bson.internal.Base64;
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
		//comprobamos si tiene foto
		byte[]foto=player.loadFoto();
		if(foto!=null)//si hay foto, la mandamos a la sesion
			sendBinary(session,foto);
		sessionsByPlayer.put(userName, session);
	}
	
	private void sendBinary(WebSocketSession session, byte[] foto) throws IOException {
		String imagen= Base64.encode(foto); //tenemos la imagen en base 64
		//ahora se la mandamos al cliente. le mandamos un json con el campo type correspondiente.
		JSONObject jso = new JSONObject();
		try {
			jso.put("TYPE", "FOTO");
			jso.put("foto", imagen);
			WebSocketMessage<?> message= new TextMessage(jso.toString());
			session.sendMessage(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
		try {
			MongoBroker.get().insertBinary("Fotos", player.getUserName(), bytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//Fotos es el nombre de la coleccion.
	
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
		
			obj.put("TYPE",	"CHAT");
			obj.put("remitente", jso.getString("remitente"));
			obj.put("contenido", jso.getString("contenido"));
			
			WebSocketMessage<?> message= new TextMessage(obj.toString());
			for(Entry<String, WebSocketSession> entry : sessionsByPlayer.entrySet()) {
				try {
				entry.getValue().sendMessage(message);
				}catch (Exception e) {
					e.printStackTrace();//dar partida por perdida y eso.
				}
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
