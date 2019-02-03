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
	private int par = 2;

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
		if (jso.getString("TYPE").equals("PPT")) {
			manejadorPPT(session, jso);
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
		//ObjectMapper mapper = new ObjectMapper();
		
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		JSONObject obj = new JSONObject();
		
		Player winner = match.getWinner();
		UUID id = match.getId();
		Board board = match.getBoard();
		
		obj.put("winner", winner);
		obj.put("id", id);
		obj.put("board", board);
		//obj = new JSONObject(mapper.writeValueAsString(match));//
		obj.put("TYPE", "PARTIDA");
		obj.put("Player1", player1.getUserName());
		obj.put("Player2", player2.getUserName());
		for (Player player : players) {
			obj.put("Tu", player.getUserName());//MIRAR SI ESTO DA CONFLICTO EN SUDOKU.
			WebSocketSession session = sessionsByPlayer.get(player.getUserName());
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			session.sendMessage(message);
		}
	}

	/**********************************************************/
	/*** Metodos que se comunican con la parte del dominio ***/
	/**
	 * @throws Exception ******************************************************/
	
	//Metodo que maneja la comunicacion del juego del Sudoku
	private void manejadorSudoku(WebSocketSession session, JSONObject jso) throws Exception {
		if(jso.getString("funcion").equals("cargar")) {
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "TABLEROINICIAL");
			obj.put("tablero", SudokuBoard.cargarTableroInicial());
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			session.sendMessage(message);
		}
		if(jso.getString("funcion").equals("marcar")) {
			UUID id = UUID.fromString(jso.getString("matchID"));
			Player jugador = (Player) session.getAttributes().get("player");
			Player oponente = Manager.get().devolverOponente(id, jugador);
			String userName = oponente.getUserName();
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
		if(jso.getString("funcion").equals("limpiar")) {
			UUID id = UUID.fromString(jso.getString("matchID"));
			Player jugador = (Player) session.getAttributes().get("player");
			Player oponente = Manager.get().devolverOponente(id, jugador);
			String userName = oponente.getUserName();
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "LIMPIAR");
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			WebSocketSession sesion = sessionsByPlayer.get(userName);
			sesion.sendMessage(message);
		}
		if(jso.getString("funcion").equals("resolver")) {
			String solucion = SudokuBoard.cargarTableroSolucion();
			String candidato = jso.getString("tablero");
			UUID id = UUID.fromString(jso.getString("matchID"));
			Player jugador = (Player) session.getAttributes().get("player");
			String userName = jugador.getUserName();
			if(solucion.equals(candidato)) {
				//Partida acabada, session.getplayer es el ganador.
				Match match = Manager.get().devolverPartido(id);
				match.setWinner(jugador);
				match.save();
				match.getBoard().end(jugador, id);
			}else {
				JSONObject obj = new JSONObject();
				obj.put("TYPE", "RESOLVERFALLIDO");
				WebSocketMessage<?> message = new TextMessage(obj.toString());
				WebSocketSession sesion = sessionsByPlayer.get(userName);
				sesion.sendMessage(message);
			}
		}
	}
	//Metodo que maneja la comunicacion del juego del Sudoku
	private void manejadorPPT(WebSocketSession session, JSONObject jso) throws Exception {
		if(jso.getString("funcion").equals("tirada")) {
			UUID id = UUID.fromString(jso.getString("matchID"));
			int opcion = jso.getInt("opcion");
			Player jugador = (Player) session.getAttributes().get("player");
			Match match = Manager.get().move(jugador, opcion);
		} else if (jso.getString("funcion").equals("FinPartida")) {
			//Quitar este if si cojo el match con el id que le paso en el jso
			//Despues busco el match en Game.InPlayMatches. y si está, hago la solucion del juego
			//Si no, no hago nada. Cuando implemente esto, se puede quitar el if-else.
		    if (par%2==0) {
		    	Player jugador = (Player) session.getAttributes().get("player");
		    	Manager.get().fin(jugador);
		    	par++;
		    }else {
		    	par++;
		    }
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

	public static void finSudokuGanador(Player player) throws JSONException, IOException {
		String userName = player.getUserName();
		JSONObject obj = new JSONObject();
		obj.put("TYPE", "WIN");
		WebSocketMessage<?> message = new TextMessage(obj.toString());
		WebSocketSession sesion = sessionsByPlayer.get(userName);
		sesion.sendMessage(message);
		sessionsByPlayer.remove(userName);
		
	}

	public static void finSudokuPerdedor(Player player) throws JSONException, IOException {
		String userName = player.getUserName();
		JSONObject obj = new JSONObject();
		obj.put("TYPE", "LOSE");
		WebSocketMessage<?> message = new TextMessage(obj.toString());
		WebSocketSession sesion = sessionsByPlayer.get(userName);
		sesion.sendMessage(message);
		sessionsByPlayer.remove(userName);
		
	}
	
	public static void esperarTirada(Player player) throws JSONException, IOException {
		// TODO Auto-generated method stub
		String userName = player.getUserName();
		JSONObject obj = new JSONObject();
		obj.put("TYPE", "WAITOPPONENT");
		WebSocketMessage<?> message = new TextMessage(obj.toString());
		WebSocketSession sesion = sessionsByPlayer.get(userName);
		sesion.sendMessage(message);
	}
	
	public static void finJugada(Player ganador, Player perdedor, String opcion0, String opcion1) throws JSONException, IOException {
		String userNameWin = ganador.getUserName();
		String userNameLose = perdedor.getUserName();
		
		JSONObject obj1 = new JSONObject();
		obj1.put("TYPE", "RESOLVERJUGADAWIN");
		obj1.put("win", opcion0);
		obj1.put("lose", opcion1);
		
		JSONObject obj2 = new JSONObject();
		obj2.put("TYPE", "RESOLVERJUGADALOSE");
		obj2.put("win", opcion0);
		obj2.put("lose", opcion1);
		
		WebSocketMessage<?> message1 = new TextMessage(obj1.toString());
		WebSocketMessage<?> message2 = new TextMessage(obj2.toString());
		
		WebSocketSession sesion1 = sessionsByPlayer.get(userNameWin);
		WebSocketSession sesion2 = sessionsByPlayer.get(userNameLose);
		
		sesion1.sendMessage(message1); 
		sesion2.sendMessage(message2);
		
	}

	public static void finJugada(Player j0, Player j1, String opcion) throws JSONException, IOException {
		JSONObject obj = new JSONObject();
		obj.put("TYPE", "RESOLVERJUGADATIE");
		obj.put("opcion", opcion);
		WebSocketMessage<?> message = new TextMessage(obj.toString());
		WebSocketSession sesion1 = sessionsByPlayer.get(j0.getUserName());
		WebSocketSession sesion2 = sessionsByPlayer.get(j1.getUserName());
		sesion1.sendMessage(message); 
		sesion2.sendMessage(message);
	}

	public static void finPPT(String estado, Player player, Player player2, UUID id) throws Exception {
		if(estado.equals("Win")) {
			String UserNamewin = player.getUserName();
			String UserNamelose = player2.getUserName();
			
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "FINWIN");
			
			JSONObject obj2 = new JSONObject();
			obj2.put("TYPE", "FINLOSE");
			
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			WebSocketMessage<?> message2 = new TextMessage(obj2.toString());
			
			WebSocketSession sesion1 = sessionsByPlayer.get(UserNamewin);
			WebSocketSession sesion2 = sessionsByPlayer.get(UserNamelose);
			
			sesion1.sendMessage(message); 
			sesion2.sendMessage(message2);
			
			Match match = Manager.get().devolverPartido(id);
			match.setWinner(player);
			match.save();
			match.getBoard().end(player, id);

			sessionsByPlayer.remove(UserNamewin);
			sessionsByPlayer.remove(UserNamelose);
			
		}else {
			String User1 = player.getUserName();
			String User2 = player2.getUserName();
			
			JSONObject obj = new JSONObject();
			obj.put("TYPE", "FINTIE");
			
			WebSocketMessage<?> message = new TextMessage(obj.toString());
			
			WebSocketSession sesion1 = sessionsByPlayer.get(User1);
			WebSocketSession sesion2 = sessionsByPlayer.get(User2);
			
			sesion1.sendMessage(message); 
			sesion2.sendMessage(message);
			
			Match match = Manager.get().devolverPartido(id);
			match.save();
			match.getBoard().end(player, id);

			sessionsByPlayer.remove(User1);
			sessionsByPlayer.remove(User2);
		}	
	}
}
