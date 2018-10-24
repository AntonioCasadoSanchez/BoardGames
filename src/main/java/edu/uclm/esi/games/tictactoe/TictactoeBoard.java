package edu.uclm.esi.games.tictactoe;

import edu.uclm.esi.games.Board;
import edu.uclm.esi.games.Player;

public class TictactoeBoard extends Board {
	private Character[][] squares;
	
	public TictactoeBoard(TictactoeMatch match) {
		super(match);
		this.squares=new Character[3][3];
	}
	
	public Character[][] getSquares() {
		return squares;
	}

	@Override
	public void move(Player player, int[] coordinates) throws Exception {
		char symbol=player==this.match.getPlayers().get(0) ? 'X' : 'O';
		int row=coordinates[0];
		int col=coordinates[1];
		if (squares[row][col]!=null)
			throw new Exception("Square occupied");
		squares[row][col]=symbol;
	}

	public Player getWinner() {
		for(Player player : this.match.getPlayers()) {
		char symbol=player==this.match.getPlayers().get(0) ? 'X' : 'O';
		int i=0;
		boolean ristra1=squares[i][0]!=null && squares[i][0]==symbol &&  
				squares[i][1]!=null && squares[i][1]==symbol && 
				squares[i][2]!=null && squares[i][2]==symbol;
		i=1;
		boolean ristra2=squares[i][0]!=null && squares[i][0]==symbol &&  
				squares[i][1]!=null && squares[i][1]==symbol && 
				squares[i][2]!=null && squares[i][2]==symbol;
		i=2;
		boolean ristra3=squares[i][0]!=null && squares[i][0]==symbol &&  
				squares[i][1]!=null && squares[i][1]==symbol && 
				squares[i][2]!=null && squares[i][2]==symbol;
		if (ristra1 || ristra2 || ristra3)
			return player;
		i=0;
		ristra1=squares[0][i]!=null && squares[0][i]==symbol &&  
				squares[1][i]!=null && squares[1][i]==symbol && 
				squares[2][i]!=null && squares[2][i]==symbol;
		i=1;
		ristra2=squares[0][i]!=null && squares[0][i]==symbol &&  
				squares[1][i]!=null && squares[1][i]==symbol && 
				squares[2][i]!=null && squares[2][i]==symbol;
		i=2;
		ristra3=squares[0][i]!=null && squares[0][i]==symbol &&  
				squares[1][i]!=null && squares[1][i]==symbol && 
				squares[2][i]!=null && squares[2][i]==symbol;
		if (ristra1 || ristra2 || ristra3)
			return player;
		ristra1=squares[0][0]!=null && squares[0][0]==symbol &&  
				squares[1][1]!=null && squares[1][1]==symbol && 
				squares[2][2]!=null && squares[2][2]==symbol;
		ristra2=squares[2][0]!=null && squares[2][0]==symbol &&  
				squares[1][1]!=null && squares[1][1]==symbol && 
				squares[0][2]!=null && squares[0][2]==symbol;
		if( ristra1 || ristra2)
			return player;
		}
		return null;
	}

	@Override
	public boolean end() {
		// TODO Auto-generated method stub
		return false;
	}
}
