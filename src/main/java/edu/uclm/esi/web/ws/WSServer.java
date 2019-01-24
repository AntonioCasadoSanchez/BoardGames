package edu.uclm.esi.web.ws;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
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
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
		/**System.out.println(message.getPayload());
		JSONObject jso=new JSONObject(message.getPayload());
		if(jso.getString("TYPE").equals("MOVEMENT")) {
			Player player = (Player) session.getAttributes().get("player");
			JSONArray coordinates = jso.getJSONArray("coordinates");
			Match match = Manager.get().move(player, coordinates);//PREGUNTAR ESTA LINEA. mas o menos ya lo entiendo
			send(match.getPlayers(), match);
			
		}**/
		JSONObject jso=new JSONObject(message.getPayload());
		if(jso.getString("TYPE").equals("MENSAJE")) {
			sendChat(session, jso);
		}
	}
	
	public static void sendChat(WebSocketSession session, JSONObject jso ) throws Exception {
		//ObjectMapper mapper=new ObjectMapper();
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
		// TODO Auto-generated method stub	
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

	/**private void enviar(Session session, String tipo, String remitente, String texto) throws JSONException {
		JSONObject jso=new JSONObject();
		try {
			jso.put("tipo", tipo);
			jso.put("remitente", remitente);
			jso.put("contenido", texto);
			session.getBasicRemote().sendText(jso.toString());
		}catch(IOException e) {
		}
	}**/
	/*@OnOpen
	public void onOpen() {
		JOptionPane.showMessageDialog(null, "Conectado");
	}*/
	/*@OnMessage
	public void recibir(Session session, String msg)  {
		JSONObject jso=new JSONObject(msg);
		if(jso.getString("tipo").equals("mensajeChat")) {
			int idPartida=jso.getInt("idPartida");
			String jugador=jso.getString("nombreJugador");
			String mensajeChat=jso.getString("mensajeUsuario");
			try {
				JSONObject mensaje=Manager.get().mensajeChat( idPartida,jugador, mensajeChat);
				
			} catch (Exception e) {
			}
		}
		
	}*/
}
