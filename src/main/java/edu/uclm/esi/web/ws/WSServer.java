package edu.uclm.esi.web.ws;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
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

import edu.uclm.esi.games.Board;
import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.games.sudoku.SudokuBoard;
import edu.uclm.esi.mongolabels.dao.MongoBroker;
import edu.uclm.esi.web.Manager;

@Component
public class WSServer extends TextWebSocketHandler {
	private static ConcurrentHashMap<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, WebSocketSession> sessionsByPlayer = new ConcurrentHashMap<>();

	/** OnOpen **/
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// if (session.getAttributes().get("tipo") != null) {
		sessionsById.put(session.getId(), session);
		Player player = (Player) session.getAttributes().get("player");
		String userName = player.getUserName();

		WebSocketSession entry = sessionsByPlayer.get(userName);
		if (entry != null) {
			// session.close no vale?
			CloseStatus status = new CloseStatus(4000);// codigo de cierre reservado para las aplicaciones
			// https://developer.mozilla.org/es/docs/Web/API/CloseEvent
			afterConnectionClosed(session, status);
			cerrarConexion(session);
		} else {
			sessionsByPlayer.put(userName, session);
		}
	}

	/** OnMessage para recibir cadenas de texto **/
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
		if (jso.getString("TYPE").equals("AVATAR")) {
			Player player = (Player) session.getAttributes().get("player");
			byte[] foto = player.loadFoto();
			if (foto != null) {
				sendBinary(session, foto);
			}
		}
		if (jso.getString("TYPE").equals("SUDOKU")) {
			manejadorSudoku(session, jso);
		}

	}


	/** OnMessage para recibir ristras binarias **/
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		Player player = (Player) session.getAttributes().get("player");
		byte[] bytes = message.getPayload().array();
		try {
			MongoBroker.get().insertBinary("Fotos", player.getUserName(), bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** OnClose **/
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		// ELIMINAMOS PRIMERO LA SESION DE LA HASHMAP DE SESIONES POR ID
		sessionsById.remove(session.getId());

		// AHORA, ELIMINAMOS LA SESION DE LA HASHMAP DE SESIONES POR USUARIO
		if (status.getCode() != 4000) {
			Player player = (Player) session.getAttributes().get("player");
			String userName = player.getUserName();
			for (Iterator<Map.Entry<String, WebSocketSession>> it2 = sessionsByPlayer.entrySet().iterator(); it2
					.hasNext();) {
				Map.Entry<String, WebSocketSession> entry2 = it2.next();
				if (entry2.getKey().equals(userName)) {
					if (entry2.getValue().equals(session)) {
						it2.remove();
					}
				}
			}
		} // Meter que si esta en partida, darla por perdida y llamar a losmetodos
			// correspondientes para perder pts y tal.
	}

	/**********************************************************/
	/** Metodos que se comunican con la parte del cliente **/
	/********************************************************/

	// Metodo que comunica a la parte del cliente datos en formato binario
	private void sendBinary(WebSocketSession session, byte[] foto) throws IOException {
		String imagen = Base64.encode(foto);
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

	// Metodo que cierra las conexiones
	public static void cerrarConexion(WebSocketSession session) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("TYPE", "CERRAR");
		WebSocketMessage<?> message = new TextMessage(obj.toString());
		session.sendMessage(message);

	}

	// Metodo que comunica a la parte del cliente un mensaje de chat
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

	// Metodo que comunica a la parte del cliente un mensaje de esperar al jugador
	public static void waitPlayer(Player player) throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put("TYPE", "WAIT");
		obj.put("mensaje", "Esperando oponente");

		WebSocketMessage<?> message = new TextMessage(obj.toString());
		WebSocketSession session = sessionsByPlayer.get(player.getUserName());
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();// Controlar esto.
		}
	}

	// Metodo que cambia la contraseña de un usuario no logueado
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

	// Metodo que cambia la contraseña de un usuario logueado
	private static void changePass(WebSocketSession session, JSONObject jso) throws JSONException, IOException {
		Player player = (Player) session.getAttributes().get("player");
		JSONObject obj = new JSONObject();
		if (player.changePass(jso)) {
			try {
				obj.put("TYPE", "PASS");
			} catch (Exception e) {
				obj.put("TYPE", "ERROR");
				obj.put("TEXTO", e.getMessage());
			}
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			session.sendMessage(message);
		}
	}

	// Metodo que se comunica con el cliente para dar comienzo la partida
	public static void inicioPartida(Vector<Player> players, Match match) throws JSONException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		JSONObject obj = new JSONObject();
		obj = new JSONObject(mapper.writeValueAsString(match));//
		obj.put("TYPE", "PARTIDA");
		obj.put("Player1", player1.getUserName());
		obj.put("Player2", player2.getUserName());
		for (Player player : players) {
			WebSocketSession session = sessionsByPlayer.get(player.getUserName());
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			session.sendMessage(message);
		}
	}

	/**********************************************************/
	/*** Metodos que se comunican con la parte del dominio ***/
	/********************************************************/
	
	//Metodo que maneja la comunicacion del juego del Sudoku
	private void manejadorSudoku(WebSocketSession session, JSONObject jso) throws JSONException, IOException {
		if(jso.getString("funcion").equals("cargar")) {
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "TABLEROINICIAL");
			obj.put("tablero", SudokuBoard.cargarTableroInicial());
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			session.sendMessage(message);
		}
		if(jso.getString("funcion").equals("marcar")) {
			UUID id = UUID.fromString(jso.getString("matchID"));
			Player p = (Player) session.getAttributes().get("player");
			Player player = Manager.get().marcar(id, p);
			String userName = player.getUserName();
			String coordI = jso.getString("coordI");
			String coordJ = jso.getString("coordJ");
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "MARCAR");
			obj.put("coordI", coordI);
			obj.put("coordJ", coordJ);
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			WebSocketSession sesion = sessionsByPlayer.get(userName);
			sesion.sendMessage(message);
		}
	}
	
	// Metodo que crea un objeto de tipo Match para crear la partida
	public static void joinGame(WebSocketSession session, JSONObject jso) throws JSONException, IOException {
		String gameName = jso.getString("juego");
		Player player = (Player) session.getAttributes().get("player");
		Match match = Manager.get().joinGame(player, gameName);
	}

	// Metodo que tendrá que ser borrado en algun momento.
	public static void send(Vector<Player> players, Match match) {
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jso;
		try {
			// jso=mapper.writeValueAsString(match);//convierte partida a cadena en formato
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
