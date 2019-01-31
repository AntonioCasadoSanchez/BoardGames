package edu.uclm.esi.web.ws;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.RequestBody;
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
	private static ConcurrentHashMap<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, WebSocketSession> sessionsByPlayer = new ConcurrentHashMap<>();

	// Porque cierra sesion(parece) cada vez que abro otra
	// Porque no hace bien controlseguridad.
	// ver error que da la consola de eclipse.
	// en mozilla se funciona diferente control de seguridad. quizas la response va
	// de otra forma???
	/** OnOpen **/
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if (session.getAttributes().get("tipo") != null) {
						
			sessionsById.put(session.getId(), session);
			Player player = (Player) session.getAttributes().get("player");
			String userName = player.getUserName();
			
			for (Iterator<Map.Entry<String, WebSocketSession>> it2 = sessionsByPlayer.entrySet().iterator(); it2
					.hasNext();) {
				Map.Entry<String, WebSocketSession> entry2 = it2.next();
				if (entry2.getValue().equals(userName)) {
					//No dejar que ws se abra.
				}

			}
			
			sessionsByPlayer.put(userName, session);
			
			/**if (sessionsByPlayer.get(userName) != null) {
				sessionsByPlayer.remove(userName);
			}**/
			// comprobamos si tiene foto
			byte[] foto = player.loadFoto();
			if (foto != null) {// si hay foto, la mandamos a la sesion
				sendBinary(session, foto);
			}
			
		} else {
session.close();
		}
	}

	private void sendBinary(WebSocketSession session, byte[] foto) throws IOException {
		String imagen = Base64.encode(foto); // tenemos la imagen en base 64
		// ahora se la mandamos al cliente. le mandamos un json con el campo type
		// correspondiente.
		JSONObject jso = new JSONObject();
		try {
			jso.put("TYPE", "FOTO");
			jso.put("foto", imagen);
			WebSocketMessage<?> message = new TextMessage(jso.toString());
			session.sendMessage(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/** OnMessage **/
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		JSONObject jso = new JSONObject(message.getPayload());

		/** Si el mensaje es del Chat **/
		if (jso.getString("TYPE").equals("MENSAJE")) {
			sendChat(session, jso);
		}
		if (jso.getString("TYPE").equals("PASSWORDTOKEN")) {
			changePassToken(session, jso);
		}
		if (jso.getString("TYPE").equals("PASSWORD")) {
			changePass(session, jso);
		}
		if (jso.getString("TYPE").equals("JUGAR")) {
			joinGame(session, jso);
		}
	}

	private static void changePass(WebSocketSession session, JSONObject jso) throws JSONException, IOException {
		Player player = (Player) session.getAttributes().get("player");
		if (player.changePass(jso)) {
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "PASS");
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			session.sendMessage(message);
		}
	}

	private static void changePassToken(WebSocketSession session, JSONObject jso) throws JSONException, IOException {
		JSONObject obj = new JSONObject();
		try {
			Player.actualizarPass(jso);
			obj.put("TYPE", "PASS");

		} catch (Exception e) {
			obj.put("TYPE", "ERROR");
			obj.put("TEXTO", e.getMessage());
		}
		WebSocketMessage<?> message = new TextMessage(obj.toString());
		session.sendMessage(message);
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		Player player = (Player) session.getAttributes().get("player");
		byte[] bytes = message.getPayload().array();
		try {
			MongoBroker.get().insertBinary("Fotos", player.getUserName(), bytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// Fotos es el nombre de la coleccion.

	/** OnClose **/
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//Meter que si esta en partida, darla por perdida y llamar a losmetodos correspondientes para perder pts y tal.
		String a="";
		for (Iterator<Map.Entry<String, WebSocketSession>> it = sessionsById.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, WebSocketSession> entry = it.next();
			if (entry.getValue().equals(session.getId())) {
				it.remove();
			}
		}

		Player player = (Player) session.getAttributes().get("player");
		String userName = player.getUserName();

		for (Iterator<Map.Entry<String, WebSocketSession>> it2 = sessionsByPlayer.entrySet().iterator(); it2
				.hasNext();) {
			Map.Entry<String, WebSocketSession> entry2 = it2.next();
			if (entry2.getKey().equals(userName)) {
				it2.remove();
			}

		}
		System.out.println("Cierre de sesion");
		// Debemos tambien sacarlos de la lista de cosas.
	}

	/*******************************************************/
	/** Metodos que son llamados desde la parte de Override **/
	/*******************************************************/

	public static void sendChat(WebSocketSession session, JSONObject jso) throws Exception {
		JSONObject obj = new JSONObject();

		obj.put("TYPE", "CHAT");
		obj.put("remitente", jso.getString("remitente"));
		obj.put("contenido", jso.getString("contenido"));

		WebSocketMessage<?> message = new TextMessage(obj.toString());
		for (Entry<String, WebSocketSession> entry : sessionsByPlayer.entrySet()) {
			try {
				entry.getValue().sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void joinGame(WebSocketSession session, JSONObject jso) throws JSONException {
		String gameName = jso.getString("juego");
		Player player = (Player) session.getAttributes().get("player");
		Match match = Manager.get().joinGame(player, gameName);
		//Match match = Manager.get().joinGame(player, gameName.substring(0, gameName.length() - 1));
		//WSServer.send(match.getPlayers(), match);
		//return match;
	}
	public static void waitPlayer(Player player) throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put("TYPE", "WAIT");
		obj.put("mensaje", "Esperando oponente");

		WebSocketMessage<?> message = new TextMessage(obj.toString());
		WebSocketSession session = sessionsByPlayer.get(player.getUserName());
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();//Controlar esto.
		}
		
	}
	public static void send(Vector<Player> players, Match match) {
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jso;
		try {
			// jso=mapper.writeValueAsString(match);//convierte partida a cadena en formato
			// json.
			jso = new JSONObject(mapper.writeValueAsString(match));
			jso.put("TYPE", "MATCH");
			for (Player player : players) {
				WebSocketSession session = sessionsByPlayer.get(player.getUserName());
				WebSocketMessage<?> message = new TextMessage(jso.toString());
				session.sendMessage(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
