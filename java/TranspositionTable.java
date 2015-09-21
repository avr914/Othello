/*
 * TranspositionTable.java
 *
 * Created on March 28, 2004, 11:24 PM
 */

import java.util.Random;

/**
 *
 * @author  ank
 */
public class TranspositionTable {
   
   /**
    * This is the transposition table.  The first 2 int slots are for white 
    * board, which is the key.  The next slot is for the value, the value type
    * (exact, alpha, beta), the best move from that point, and the depth.
    */
   private int[][][] table;
   private static final int bucketSize = 4;
   
   /**
    * Zobrist key implementation from 
    * http://www.seanet.com/~brucemo/topics/zobrist.htm
    */
   private static final long[][] zobristKey;
   private static final long[][] mask;
   private static final int SEARCH_AGAIN_FLAG = 0x80000000;
   static {
      Random rand = new Random();
      zobristKey = new long[2][64];
      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 64; j++)
            zobristKey[i][j] = rand.nextLong(); 
      }
      
      mask = new long[8][8];
      for (int i = 0; i < 8; i++)
         for (int j = 0; j < 8; j++)
            mask[i][j] = (1L << ((i << 3) + j));
   }
      
   public int entries = 0;
   
   private boolean tableCleaned = false;
   
   /* Bit 32 (sign bit) = EXACT VALUE
    * Bit 31 = ALPHA VALUE
    * Bit 30 = BETA VALUE
    * Bit 29 = Best move boolean
    * Bits 23-28 = The move (0-63)
    * Bits 17-22 = Depth
    * Bits 0-16 = Score
    */
   private static final int EXACT_FLAG = 1 << 31;
   private static final int ALPHA_FLAG = 1 << 30;
   private static final int BETA_FLAG = 1 << 29;
   private static final int MOVE_FLAG = 1 << 28;
   private static final int MOVE_DATA = 0x0FC00000;
   private static final int DEPTH_DATA = 0x003F0000;
   private static final int SCORE_DATA = 0x0000FFFF;
   
   // Some public constants
   public static final int NO_MOVE = -1;
   public static final int EXACT = 0;
   public static final int ALPHA = 1;
   public static final int BETA = -1;
   
   /**
    * Creates a table of size 2^17 - 1.  This is a prime number and is useful 
    * for a good hashing function.
    */
   public TranspositionTable() {
      table = new int[1000003][4][3];
   }
   
   public void addEntry(long white, long black, int moveRow, int moveCol, 
            int searchPieces, int actualPieces) {
      
      long key = 0;
      for (int i = 0; i < 8; i++)
         for (int j = 0; j < 8; j++) {
            key ^= ((white & mask[i][j]) != 0) ? zobristKey[0][(i << 3) + j] :
               (((black & mask[i][j]) != 0) ? zobristKey[1][(i << 3) + j] : 0);
         }
      
      int slot = (int) (key % ((long) table.length));
      slot = (slot < 0) ? -slot : slot;   // Make it positive
      
      // Check for collision
      boolean isFilled = true;
      int pieces = 0;
      int leastPieces = 65;
      int bucketNum = 0;
      int earliest = 0;
      
      // If the keys match, do nothing
      for (int i = 0; i < bucketSize; i++) {
         if ((int) (key >> 32) == table[slot][i][0] && ((int) key == table[slot][i][1]))
            return;
      }
      
      // If the keys don't match, do a quadratic expansion
      for (int i = 0; isFilled == true && i < bucketSize; i++) {
         pieces = (table[slot][i][2] & 0xFF000000) >> 24;
         if (pieces < leastPieces)
            earliest = i;
         
         if ((table[slot][i][0] == 0 && table[slot][i][1] == 0)) {
            isFilled = false;
            bucketNum = i;
         }
         
         if (i == (bucketSize - 1) && isFilled == true) {
            entries--;
            bucketNum = earliest;
         }
      }
      
      // Add the entry
      table[slot][bucketNum][0] = (int) (key >> 32);
      table[slot][bucketNum][1] = (int) key;
      table[slot][bucketNum][2] = ((moveRow << 3) + moveCol) + (searchPieces << 24);
      entries++;
   }
   
   public int getEntry(long white, long black) {      
      long key = 0;
      for (int i = 0; i < 8; i++)
         for (int j = 0; j < 8; j++) {
            key ^= ((white & mask[i][j]) != 0) ? zobristKey[0][(i << 3) + j] :
               (((black & mask[i][j]) != 0) ? zobristKey[1][(i << 3) + j] : 0);
         }
      
      int slot = (int) (key % ((long) table.length));
      slot = (slot < 0) ? -slot : slot;   // Make it positive
      
      // Check if the data is correct
      int key1 = (int) (key >> 32);
      int key2 = (int) key;
      
      for (int i = 0; i < bucketSize; i++) {
         if (key1 == table[slot][i][0] && (key2 == table[slot][i][1])) {
            return (table[slot][i][2] & 0x00FFFFFF);
         }
      }
      
      return NO_MOVE;
   }
   
   public void cleanUp(int piecesThreshold) {
      int pieces;
      for (int i = 0; i < table.length; i++) {
         for (int j = 0; j < bucketSize; j++) {
            pieces = (table[i][j][2] & 0xFF000000) >> 24;
            if (pieces != 0 && pieces < piecesThreshold) {
               table[i][j][0] = table[i][j][1] = table[i][j][2] = 0;
               entries--;
            }
         }
      }
      
      tableCleaned = true;
   }
   
//   public void setTableCleaned(boolean b) {
//      tableCleaned = b;
//   }
   
   public static void main(String[] args) {
      int test = 36 + (24 << 24);
      System.out.println("square = " + (test & 0x00FFFFFF));
      System.out.println("moves = " + ((test & 0xFF000000) >> 24));
   }
}
