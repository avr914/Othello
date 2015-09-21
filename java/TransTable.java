import java.util.Hashtable;
import java.util.Random;

public class TransTable {
   long[][] pieces = new long[3][64];
   long hash;
   int current_depth;
   double score;
   long tableIndex = hash % (3*64);
   Hashtable<Long,Double> numbers = new Hashtable<Long,Double>();
   private final static int BLACK = 0;
   private final static int WHITE = 1;
   private final static int NONE = 2;
   OptimizedOthelloBoard board = new OptimizedOthelloBoard();
   OthelloSide side;
   public TransTable() {
      Random gen = new Random();
      hash = 0L;
      for(int i = 0; i < 3; i++) {
      		for(int j = 0; j < 64; j++){
      			pieces[i][j] = gen.nextLong();
      			hash ^= pieces[i][j];
      		}
      }
   }
   
   public static TransTable getDefaultBoard() {
	   TransTable table = new TransTable();
	   table.hash ^= table.pieces[WHITE][getPieceIndex(3,3)];
	   table.hash ^= table.pieces[WHITE][getPieceIndex(4,4)];
	   table.hash ^= table.pieces[BLACK][getPieceIndex(4,3)];
	   table.hash ^= table.pieces[BLACK][getPieceIndex(3,4)];
	   return table;
	   
   }
   
   public static int getPieceIndex(int x, int y) {
	   return (x + 8*y);
   }
   
   private void hash() {
      for(int i = 0; i < 3; i++) {
      		for(int j = 0; j < 64; j++) {
      			hash ^= pieces[i][j];
      		}
      }
   }
   
   public void setDepth(int depth) {
	   current_depth = depth;
   }
   
   public long getHash() {
	   return hash;
   }
   
}