package edu.uclm.esi.web;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;

@RestController
public class MatchControllerGet {
	
	@RequestMapping(value="/move", method=RequestMethod.GET)
	public Match move(HttpSession session, @RequestParam(value="coordinate") int[] coordinates) throws Exception {
		Player player=(Player) session.getAttribute("player");
		try {
			if (player==null)
				throw new Exception("You need to be logged and to be in a match");
			return player.move(coordinates);
		}catch(Exception e) {
			InvalidMatch error = new InvalidMatch(e.getMessage());
			return error;
		}
		
	}
}
