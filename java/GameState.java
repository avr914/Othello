import java.math.BigInteger;
import java.util.*;

public class GameState {
	ZobristHash hash;
	BigInteger state;
	OptimizedOthelloBoard board = new OptimizedOthelloBoard();
	private final static int BLACK = 0;
	private final static int WHITE = 1;
	public GameState() {
		state = BigInteger.valueOf(0);
	}
	public GameState(ZobristHash in) {
		hash = in;
		hash.hash();
		state = hash.hash;
	}
	public GameState(ZobristHash in, OptimizedOthelloBoard inBoard) {
		System.out.println("Start of constructor call");
		hash = in;
		hash.hash();
		state = hash.hash;
		long black = inBoard.black;
		long white = inBoard.white;
		System.out.println("Con: First Hash: " + state);
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if((black & 1L) != 0) { 
					System.out.println("black @ " + y + "," + x);
					state = state.xor(hash.pieces[BLACK][getPieceIndex(y,x)]);
					System.out.println("Con: Black Hash: " + state);
				}
				if((white & 1L) != 0) {
					System.out.println("white @ " + x + "," + y);
					state = state.xor(hash.pieces[WHITE][getPieceIndex(y,x)]);
					System.out.println("Con: White Hash: " + state);
				}
				black = black >> 1;
				white = white >> 1;
			}
		}
	}
	public void defaultBoard() {
		state = state.xor(hash.pieces[WHITE][getPieceIndex(3,3)]);
		state = state.xor(hash.pieces[WHITE][getPieceIndex(4,4)]);
		state = state.xor(hash.pieces[BLACK][getPieceIndex(4,3)]);
		state = state.xor(hash.pieces[BLACK][getPieceIndex(3,4)]);

	}	
	
	public static GameState getDefaultBoard(ZobristHash in) {
		GameState stuff = new GameState(in);
		stuff.state = stuff.state.xor(in.pieces[WHITE][getPieceIndex(3,3)]);
		stuff.state = stuff.state.xor(in.pieces[WHITE][getPieceIndex(4,4)]);
		stuff.state = stuff.state.xor(in.pieces[BLACK][getPieceIndex(4,3)]);
		stuff.state = stuff.state.xor(in.pieces[BLACK][getPieceIndex(3,4)]);
		return stuff;
	}
	
	public GameState copy() {
		GameState stat = new GameState();
		stat.state = state;
		stat.hash = hash;
		return stat;
	}
	
	public static int getPieceIndex(int x, int y) {
		return (x + 8*y);
	}
	
	

	void oldupdate(int side, Move m) {
	  	int x = m.getX();
	  	int y = m.getY();
	  	int opp = (1 - side);
	  	for (int dx = -1; dx <= 1; dx++) {
	  		for (int dy = -1; dy <= 1; dy++) {
	           if (dy == 0 && dx == 0) {
	              continue;
	           }
	           x = m.getX();
	           y= m.getY();
	           do {
	              x += dx;
	              y += dy;
	           } while (board.isOnBoard(x,y) && board.get(opp,x,y));
	           if (board.isOnBoard(x,y) && board.get(side,x,y)) {
	              x = m.getX();
	              y = m.getY();
	              x += dx;
	              y += dy;
	              while (board.isOnBoard(x,y) && board.get(opp,x,y)) {
	                 state = state.xor(hash.pieces[opp][x + 8*y]);
	                 state = state.xor(hash.pieces[side][x + 8*y]);
	                 x += dx;
	                 y += dy;
	              }
	           }
	        }
	     }
	  	state = state.xor(hash.pieces[side][x+8*y]);
	  }
	
	public void makeMove(int side, int x, int y) {
        
        int otherSide = (1 - side);
        
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dy == 0 && dx == 0) dy = 1;
                
                int tempX = x + dx, tempY = y + dy;
                
                for(;board.isOnBoard(tempX, tempY) && (board.getState(tempX, tempY) == otherSide);
                tempX += dx, tempY += dy); {
                
                if (board.isOnBoard(tempX, tempY) && (board.getState(tempX, tempY) == side)) {
                    for(tempX = x + dx, tempY = y + dy;
                    board.isOnBoard(tempX, tempY) && (board.getState(tempX, tempY) == otherSide);
                    tempX += dx, tempY += dy) {
                    	board.setState(side, tempX, tempY);
                    	state = state.xor(hash.pieces[side][x + 8*y]);
        
                    }
                }
                }
            }
        }
        
        board.setState(side, x, y);
	}
    
	
	public void update(int side, Move m) {
		OptimizedOthelloBoard proxy = board.copy();
		proxy.makeMove(side, m.getX(), m.getY());
		long[] temp = board.compareTo(proxy);
		long black_dif = temp[BLACK];
		long white_dif = temp[WHITE];
		long blackcopy = board.black;
		long whitecopy = board.white;
		System.out.print("black_dif: ");
		System.out.printf("%064d\n", new BigInteger(Long.toBinaryString(black_dif)));
		System.out.print("white_dif: ");
		System.out.printf("%064d\n", new BigInteger(Long.toBinaryString(white_dif)));
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				if((black_dif & 1L) != 0) {
					System.out.println("Black XOR @ (" + x + "," + y + ")");
					if((blackcopy & 1L) != 0) {
						state = state.xor(hash.pieces[BLACK][getPieceIndex(x,y)]);
						state = state.xor(hash.pieces[WHITE][getPieceIndex(x,y)]);
					} else if((whitecopy & 1L) != 0){
						state = state.xor(hash.pieces[WHITE][getPieceIndex(x,y)]);
						state = state.xor(hash.pieces[BLACK][getPieceIndex(x,y)]);
					} else {
						state = state.xor(hash.pieces[BLACK][getPieceIndex(x,y)]);
					}
				}
				if((white_dif & 1L) != 0) {
					System.out.println("THERE");
					if((whitecopy & 1L) != 0) {
						state = state.xor(hash.pieces[WHITE][getPieceIndex(x,y)]);
						state = state.xor(hash.pieces[BLACK][getPieceIndex(x,y)]);
					} else if((blackcopy & 1L) != 0){
						state = state.xor(hash.pieces[BLACK][getPieceIndex(x,y)]);
						state = state.xor(hash.pieces[WHITE][getPieceIndex(x,y)]);
					} else {
						state = state.xor(hash.pieces[WHITE][getPieceIndex(x,y)]);
					}
				}
				black_dif >>= 1;
				white_dif >>= 1;
	  			proxy.black >>= 1;
	  			proxy.white >>= 1;
	  			blackcopy >>= 1;
	  			whitecopy >>= 1;
			}
		}
	}
}
