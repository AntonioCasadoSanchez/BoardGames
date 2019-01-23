package edu.uclm.esi.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.uclm.esi.games.Match;
import edu.uclm.esi.games.Player;
import edu.uclm.esi.web.ws.WSServer;

@RestController
public class UserControllerPost {
  
//  @RequestMapping(value="/register", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//  public JSONObject register( String email,String userName,  String pwd1, String pwd2) throws Exception {
  //  if (!pwd1.equals(pwd2))
  //    return new JSONObject().put("mensaje", "las contraseñas no coinciden");
  //  Player player=Player.register(email, userName, pwd1);
      //return player;
  //  return new JSONObject().put("mensaje","ok");
//  }
  
  @RequestMapping(value="/register", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public Player register( String email,String userName,  String pwd1, String pwd2) throws Exception {
    if (!pwd1.equals(pwd2))
      throw new Exception ("Error: las contrase�ass no coinciden");
    if (email.length()==0)
		throw new Exception("El email no puede ser vac�o");
    if (pwd1.length()<3)
		throw new Exception("La contrase�a tiene que tener 4 caracteres por lo menos");
    Player player=Player.register(email, userName, pwd1);
      return player;//y esto?asdf
  }
  
  @ExceptionHandler(Exception.class)
  public ModelAndView handleERROR(HttpServletRequest req, Exception ex) {
    ModelAndView result= new ModelAndView();
    result.setViewName("respuesta");
    result.addObject("exception",ex);
    result.setStatus(HttpStatus.UNAUTHORIZED);
    return result;
  }
  @RequestMapping(value="/login", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public Player loginPost(HttpSession session, String userName, String pwd) throws Exception {
    Player player=Player.identify(userName, pwd);
    session.setAttribute("player", player);//Si estas haciendo un session.setatribute player, porque luego haces un return player?
    return player;
  }
  
  @RequestMapping(value= {"/joinGame", "/post/joinGame"}, method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE) 
  public Match joinGamePost(HttpSession session, @RequestBody String gameName) throws Exception {
    Player player=(Player) session.getAttribute("player");
    if (player==null)
      throw new Exception("You need to be logged");
    Match match=Manager.get().joinGame(player, gameName.substring(0, gameName.length()-1));
  WSServer.send(match.getPlayers(), match);
    return match;
  }
}
