import java.math.BigInteger;
import java.util.*;

public class ZobristHash {

	BigInteger[][][] pieces = new BigInteger[2][8][8];
	   BigInteger hash;
	   BigInteger tableIndex;
	   private final int BLACK = 0;
	   private final int WHITE = 0;
	   OptimizedOthelloBoard board = new OptimizedOthelloBoard();
	   public ZobristHash() {
	      Random gen = new Random();
	      BigInteger r;
	      for(int i = 0; i < 2 ; i++) {
	      		for(int x = 0; x < 8; x++){
	      			for(int y = 0; y <8; y++) {
	      				r = new BigInteger(64, gen);
	      				pieces[i][x][y] = r;
	      			}
	      		}
	      }
	   }
	   
	   public static int getPieceIndex(int x, int y) {
		   return (x + 8*y);
	   }
	   
	   public void hash() {
		   hash = pieces[BLACK][0][0];
	      for(int i = 0; i < 2; i++) {
	      		for(int x = 0; x < 8; x++) {
	      			for(int y = 0; y < 8; y++) {
	      				hash = hash.xor((pieces[i][x][y]));
	      			}
	      		}
	      }
	      tableIndex = hash.mod(BigInteger.valueOf(2*64));
	   }
	   
	   public BigInteger getHash() {
		   return hash;
	   }
	
}
