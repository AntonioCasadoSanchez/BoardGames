package edu.uclm.esi.web;

import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.web.ws.WSServer;

@RestController
public class UserControllerPost {
	
	private String hola;
	@RequestMapping(value="/login", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public Player loginPost(HttpSession session, String userName, String pwd) throws Exception {
		Player player=Player.identify(userName, pwd);
		session.setAttribute("player", player);
		return player;
	}
	
	@RequestMapping(value= {"/joinGame", "/post/joinGame"}, method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE) 
	public Match joinGamePost(HttpSession session, @RequestBody String gameName) throws Exception {
		Player player=(Player) session.getAttribute("player");
		if (player==null)
			throw new Exception("You need to be logged");
		Match match=Manager.get().joinGame(player, gameName.substring(0, gameName.length()-1));
		WSServer.send(match.getPlayers(),match);
		return match;
	}
	@RequestMapping(value="/loginP", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String loginP(HttpSession session, String userName, String pwd) throws Exception {
		hola = "Holaaa";
		System.out.println(hola);
		return hola;
		
	}
}
