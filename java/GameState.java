import java.math.BigInteger;

/**
 * This class implements my own GameState.
 * Currently, it is not integrated with any of the Othello players.
 * @author Arvind Vijayakumar
 */

public class GameState {
	private long hashCode;
	public OthelloBoard gameBoard;
	
	public GameState() {
		hashCode = ZobristHash.INSTANCE.board;
	}
	public GameState(OthelloBoard gameBoard) {
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if(gameBoard.get(OthelloSide.BLACK, x, y)) 
					hashCode ^= ZobristHash.INSTANCE.pos[ZobristHash.INSTANCE.BLACK][8*x + y];
				else if(gameBoard.get(OthelloSide.WHITE, x, y))
					hashCode ^= ZobristHash.INSTANCE.pos[ZobristHash.INSTANCE.WHITE][8*x + y];
			}
		}
		this.gameBoard = gameBoard.copy();
	}
	
	public GameState copy() {
		GameState gs = new GameState();
		gs.setHashCode(hashCode);
		gs.gameBoard = this.gameBoard.copy();
		return gs;
	}
	
	public GameState doMove(Move m, OthelloSide turn) {
		GameState gs = this.copy();
		int black = ZobristHash.INSTANCE.BLACK;
		int white = ZobristHash.INSTANCE.WHITE;
		if (m == null)
	        return this;

	      if (!gs.gameBoard.checkMove(m, turn)) {
	            throw new InternalError("Invalid Move " + m);
	      }

	      OthelloSide other = turn.opposite();
	      for (int dx = -1; dx <= 1; dx++) {
	         for (int dy = -1; dy <= 1; dy++) {
	            if (dy == 0 && dx == 0) {
	               continue;
	            }
	            int x = m.getX();
	            int y = m.getY();
	            do {
	               x += dx;
	               y += dy;
	            } while (gs.gameBoard.onBoard(x,y) && gs.gameBoard.get(other,x,y));
	            if (gs.gameBoard.onBoard(x,y) && gs.gameBoard.get(turn,x,y)) {
	               x = m.getX();
	               y = m.getY();
	               x += dx;
	               y += dy;
	               while (gs.gameBoard.onBoard(x,y) && gs.gameBoard.get(other,x,y)) {
            		  if(gs.gameBoard.occupied(x, y)) {
            			  if(gs.gameBoard.get(OthelloSide.BLACK, x, y)) {
            				  gs.hashCode ^= ZobristHash.INSTANCE.pos[black][x*8 + y];
            				  gs.hashCode ^= ZobristHash.INSTANCE.pos[white][x*8 + y];
            			  } else {
            				  gs.hashCode ^= ZobristHash.INSTANCE.pos[white][x*8 + y];
            				  gs.hashCode ^= ZobristHash.INSTANCE.pos[black][x*8 + y];
            			  }
            		  } else {
            			  if(gs.gameBoard.get(OthelloSide.BLACK, x, y)) {
            				  gs.hashCode ^= ZobristHash.INSTANCE.pos[black][x*8 + y];
            			  } else {
            				  gs.hashCode ^= ZobristHash.INSTANCE.pos[white][x*8 + y];
            			  }
            		  }
	            	  gs.gameBoard.set(turn,x,y);
	                  x += dx;
	                  y += dy;
	               }
	            }
	         }
	      }
	      gs.gameBoard.set(turn,m.getX(),m.getY());
	      gs.hashCode ^= ZobristHash.INSTANCE.pos[black][m.getX()*8 + m.getY()];
		return gs;
	}
	
	public long getHashCode() {
		return hashCode;
	}
	
	public void setHashCode(long newHashCode) {
		hashCode = newHashCode;
	}
	
	/*
	//@override
	public int hashCode() {
		return hashCode.;
	}
	*/

}
