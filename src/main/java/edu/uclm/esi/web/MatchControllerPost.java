package edu.uclm.esi.web;

import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;

@RestController
public class MatchControllerPost {
	
	@RequestMapping(value="/move", method=RequestMethod.POST, consumes= {MediaType.APPLICATION_JSON_VALUE})
	public Match move(HttpSession session, @RequestBody int[] coordinates) throws Exception {
		Player player=(Player) session.getAttribute("player");
		if (player==null)
			throw new Exception("You need to be logged and to be in a match");
		return player.move(coordinates);
	}
}
