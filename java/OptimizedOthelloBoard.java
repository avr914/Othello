
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Random;

/**
 * Implements a standard 8x8 Othello board.
 * <strong>TODO:</strong> Streamline.
 *
 * @author Brandon Moore
 * @author Joseph Gonzalez
 **/

public class OptimizedOthelloBoard {
    // Static Data Members ====================================================>
    /** Used to indicate the state of the sqaure */
    public final static int EMPTY = 2, WHITE = 0, BLACK = 1;
    
    public final static int ADJ_CORNER_BONUS = 10;
    
    public final static int START_MIDDLE_STRAT = 22, START_END_STRAT = 60;
    
    public final static int[][] beginMatrix =
    {{60,-9, 5, 5, 5, 5,-9,60},
     {-9,-20,-2,-2,-2,-2,-20,-9},
     {5, -2,-1,-1,-1,-1,-2, 5},
     {5, -2,-1,-1,-1,-1,-2, 5},
     {5, -2,-1,-1,-1,-1,-2, 5},
     {5, -2,-1,-1,-1,-1,-2, 5},
     {-9,-20,-2,-2,-2,-2,-20,-9},
     {60,-9, 5, 5, 5, 5,-9,60}};
     public final static int[][] middleMatrix =
     {{50,-5, 7, 7, 7, 7,-5, 50},
      {-5,-10, 3, 3, 3, 3,-10,-5},
      {7,  3, 3, 3, 3, 3, 3,  7},
      {7,  3, 3, 5, 5, 3, 3,  7},
      {7,  3, 3, 5, 5, 3, 3,  7},
      {7,  3, 3, 3, 3, 3, 3,  7},
      {-5,-10, 3, 3, 3, 3,-10,-5},
      {50,-2, 7, 7, 7, 7,-5, 50}};
      
      public final static int[][] endMatrix = new int[8][8];
      
      static {
          for(int x = 0; x <= 7; x++)
              for(int y = 0; y <= 7; y++){
                  endMatrix[x][y] = 30;
              }
      }
      
      // Data Members ===========================================================>
      /**
       * This is the set of bits storing the locations that have a white pieces if
       * the value is 1 then the piece is white otherwise it is either black or
       * empty
       **/
      public long white = 0L;
      
      /**
       * This is the set of bits storing the locations that have a black pieces if
       * the value is 1 then the piece is black otherwise it is either white or
       * empty
       **/
      public long black = 0L;
      
      public int numPieces = 0;
      
      public static OptimizedOthelloBoard getDefaultBoard() {
          OptimizedOthelloBoard bd = new OptimizedOthelloBoard();
          //Standard setup with 4 pieces in the center.
          bd.setState(WHITE, 3, 3);
          bd.setState(WHITE, 4, 4);
          bd.setState(BLACK, 4, 3);
          bd.setState(BLACK, 3, 4);
          return bd;
      }
      
      // Constructors ===========================================================>
      public OptimizedOthelloBoard(){}
      
      public OptimizedOthelloBoard(OptimizedOthelloBoard board){
          black = board.black;
          white = board.white;
          numPieces = board.numPieces;
      }
      
      // Methods ================================================================>
      
      public int rawScore(int side) {
    	  int score = 0;
    	  long board, otherBoard;
          if (side == WHITE) {
              board = white;
              otherBoard = black;
          }
          else {
              board = black;
              otherBoard = white;
          }
          for (int x = 0; x <= 7; x++)
              for (int y = 0; y <= 7; y++) {
                  score += (board & 1L);
                  board = board >> 1;
              }
    	  return score;
      }
      
      public boolean isCorner(int x, int y) {
    	  int temp = x + 8*y;
    	  if (temp == 0)
    		  return true;
    	  if (temp == 7)
    		  return true;
    	  if (temp == 56)
    		  return true;
    	  if (temp == 63)
    		  return true;
    	  return false;
      }
      
      public boolean isCorner(Move m) {
    	  int x = m.getX();
    	  int y = m.getY();
    	  int temp = x + 8*y;
    	  if (temp == 0)
    		  return true;
    	  if (temp == 7)
    		  return true;
    	  if (temp == 56)
    		  return true;
    	  if (temp == 63)
    		  return true;
    	  return false;
      }
      
      public int evaluateBoard(int side) {
          int score = 0;
          int evalMatrix[][];
          if (numPieces < START_MIDDLE_STRAT)
              evalMatrix = beginMatrix;
          else if (numPieces < START_END_STRAT)
              evalMatrix = middleMatrix;
          else
              evalMatrix = endMatrix;
          
          long board, otherBoard;
          if (side == WHITE) {
              board = white;
              otherBoard = black;
          }
          else {
              board = black;
              otherBoard = white;
          }
          
          if (board == 0)
              return -100000;
          if (otherBoard == 0)
              return 100000;
          
          //Check corners to see if we have them.  if so, ones next to corners desirable.
          if ((board & 1L) > 0)
              score += ADJ_CORNER_BONUS * (((board >> 1) & 1L) + ((board >> 9) & 1L) + ((board >> 8) & 1L));
          if (((board >> 7) & 1L) > 0)
              score += ADJ_CORNER_BONUS * (((board >> 6) & 1L) + ((board >> 14) & 1L) + ((board >> 15) & 1L));
          if (((board >> 56) & 1L) > 0)
              score += ADJ_CORNER_BONUS * (((board >> 48) & 1L) + ((board >> 49) & 1L) + ((board >> 57) & 1L));
          if (((board >> 63) & 1L) > 0)
              score += ADJ_CORNER_BONUS * (((board >> 62) & 1L) + ((board >> 55) & 1L) + ((board >> 54) & 1L));
          
          for (int x = 0; x <= 7; x++)
              for (int y = 0; y <= 7; y++) {
                  score += evalMatrix[x][y] * (board & 1L);
                  score -= evalMatrix[x][y] * (otherBoard & 1L);
                  board = board >> 1;
              }
          
          return score;
      }
      
      public boolean occupied(int x, int y) {
          return (((white | black) >> (x + 8 * y)) & 1L) != 0;
      }
      
      public boolean get(int side, int x, int y) {
    	  long tempBoard;
    	  if(side == BLACK) {
    		  tempBoard = black;
    	  } else {
    		  tempBoard = white;
    	  }
    	  return ((tempBoard >> (x + 8 * y)) & 1L) != 0;
      }
      
      public int getState(int x, int y) {
          return ((((white | black) >> (x + (y << 3))) & 1L) == 0L)? EMPTY : (int)((black >> (x + (y << 3))) & 1L);
      }
      
      public void setState(int state, int x, int y) {
          if(state == WHITE) {
              white = (white | (1L << (x + 8 * y)));
              black = (black & ~(1L << (x + 8 * y)));
          } else if(state == BLACK) {
              black = (black | (1L << (x + 8 * y)));
              white = (white & ~(1L << (x + 8 * y)));
          } else {
              black = (black & (~(1L << (x + 8 * y))));
              white = (white & (~(1L << (x + 8 * y))));
              
          }
      }
      
      /** Returns a copy of this board. */
      public OptimizedOthelloBoard copy() {
          OptimizedOthelloBoard newBoard = new OptimizedOthelloBoard();
          newBoard.black = black;
          newBoard.white = white;
          newBoard.numPieces = numPieces;
          return newBoard;
      }
      
      public boolean isGameOver() {
          return !(hasMoves(BLACK) || hasMoves(WHITE));
      }
      
      public boolean hasMoves(int side) {
          for (int x = 0; x < 8; x++)
              for (int y = 0; y < 8; y++)
                  if(isLegalMove(side, x, y)) return true;
          return false;
      }
      
      boolean isOnBoard(int x, int y) {
          return(0 <= x && x < 8 && 0 <= y && y < 8);
      }
      
      
      public boolean isLegalMove(int side, int x, int y) {
          // Make sure the square hasn't already been taken.
          if((((white | black) >> (x + 8 * y)) & 1L) != 0) return false;
          
          int otherSide = (1 - side);
          
          for (int dx = -1; dx <= 1; dx++) {
              for (int dy = -1; dy <= 1; dy++) {
                  if (dy == 0 && dx == 0) dy = 1;
                  
                  int tempX = x + dx, tempY = y + dy;
                  
                  // Test if this piece at temp x is not empty and is the other side
                  if( ((((white | black) >> (tempX + (tempY << 3))) & 1L) != 0L) &&
                  ((int)((black >> (tempX + (tempY << 3))) & 1L) == otherSide) )  {
                      tempX += dx;
                      tempY += dy;
                      for(;(0 <= tempX && tempX < 8 && 0 <= tempY && tempY < 8)
                      && ((((white | black) >> (tempX + (tempY << 3))) & 1L) != 0L);
                      tempX +=dx, tempY += dy) {
                          if(((((white | black) >> (tempX + (tempY << 3))) & 1L) != 0L) &&
                          ((int)((black >> (tempX + (tempY << 3))) & 1L) == side))
                              return true;
                      } // end of while loop
                  } // end of if statement
              } // end of for dy
          } // end of for dx
          return false;
      }
      
      public void makeMove(int side, int x, int y) {
          numPieces++;
          
          int otherSide = (1 - side);
          
          for (int dx = -1; dx <= 1; dx++) {
              for (int dy = -1; dy <= 1; dy++) {
                  if (dy == 0 && dx == 0) dy = 1;
                  
                  int tempX = x + dx, tempY = y + dy;
                  
                  for(;isOnBoard(tempX, tempY) && (getState(tempX, tempY) == otherSide);
                  tempX += dx, tempY += dy);
                  
                  if (isOnBoard(tempX, tempY) && (getState(tempX, tempY) == side)) {
                      for(tempX = x + dx, tempY = y + dy;
                      isOnBoard(tempX, tempY) && (getState(tempX, tempY) == otherSide);
                      tempX += dx, tempY += dy) setState(side, tempX, tempY);
                  }
              }
          }
          
          setState(side, x, y);
      } // end of makeMove
      
      public String toString(){
          String output = new String();
          for(int y = 0; y < 8; y++){
              for(int x = 0; x < 8; x++){
                  output += " | " + getState(x,y);
              }
              output += " |\n----------------------------------\n";
          }
          return output;
      }
      
      public static void printLong(long num){
          String output = new String();
          for(int y = 0; y < 8; y++){
              for(int x = 0; x < 8; x++){
                  output += " | " + ((num >> (x + 8 * y)) & 1L) ;
              }
              output += " |\n----------------------------------\n";
          }
          System.out.println("\n" + output);
      }
      
      public long[] compareTo(OptimizedOthelloBoard comp) {
    	  long black_dif = black ^ comp.black;
    	  long white_dif = white ^ comp.white;
    	  long[] temp = new long[2];
    	  final int BLACK_POS = 0;
    	  final int WHITE_POS = 1;
    	  temp[BLACK_POS] = black_dif;
    	  temp[WHITE_POS] = white_dif;
    	  return temp;
    	  
      }
      
      public static void main(String args[]){
    	  //Tests OptimizedOthelloBoard + toString() ========> DONE
    	  /*OptimizedOthelloBoard bd = new OptimizedOthelloBoard();
          bd.setState(BLACK,2,3);
          bd.setState(WHITE,3,3);
          bd.setState(WHITE,4,3);
          System.out.println(bd.isLegalMove(BLACK,5,3));
          bd.makeMove(BLACK,5,3);
          System.out.println(bd);*/
          //Testing .compareTo ======> 
    	  /*OptimizedOthelloBoard foo = new OptimizedOthelloBoard();
          foo = getDefaultBoard();
          System.out.println("foo:");
          System.out.println(foo);
          OptimizedOthelloBoard bar = new OptimizedOthelloBoard();
          bar = getDefaultBoard();
          bar.makeMove(BLACK, 2, 3);
          System.out.println("bar:");
          System.out.println(bar);
          long[] temp = foo.compareTo(bar);
          System.out.print("foo_black: ");
          System.out.printf("%064d\n", new BigInteger(Long.toBinaryString((long)foo.black)));
          System.out.print("foo_white: ");
          System.out.printf("%064d\n", new BigInteger(Long.toBinaryString((long)foo.white)));
          System.out.print("bar_black: ");
          System.out.printf("%064d\n", new BigInteger(Long.toBinaryString((long)bar.black)));
          System.out.print("bar_white: ");
          System.out.printf("%064d\n", new BigInteger(Long.toBinaryString((long)bar.white)));
          System.out.print("black_dif: ");
          System.out.printf("%064d\n", new BigInteger(Long.toBinaryString((long)temp[0])));
          System.out.print("white_dif: ");
          System.out.printf("%064d\n", new BigInteger(Long.toBinaryString((long)temp[1])));*/
    	  //Tests GameStates and ZobristHash =====> 
    	  ZobristHash hash = new ZobristHash();
    	  OptimizedOthelloBoard newBoard = getDefaultBoard();
    	  GameState temp = new GameState(hash, newBoard);
    	  Move m = new Move(2,3);
    	  newBoard.makeMove(BLACK, m.getX(), m.getY());
    	  GameState stat = new GameState(hash, newBoard);
    	  System.out.println("Original Stat State: " + temp.state);
    	  System.out.println("Final Stat State: " + stat.state);
    	  GameState two = GameState.getDefaultBoard(hash);
    	  System.out.println("Original Two State: " + two.state);
    	  two.state = two.state.xor(hash.pieces[WHITE][GameState.getPieceIndex(3,3)]);
    	  System.out.println("Two State (3,3) XOR WHITE: " + two.state);
    	  two.state = two.state.xor(hash.pieces[BLACK][GameState.getPieceIndex(3,3)]);
    	  System.out.println("Two State (3,3) XOR BLACK: " + two.state);
    	  two.state = two.state.xor(hash.pieces[BLACK][GameState.getPieceIndex(3,2)]);
    	  System.out.println("Two State (2,3) XOR BLACK: " + two.state);
    	  System.out.println("Final Two State: " + two.state);
    	  //Tests XOR =====> DONE
    	  /*BigInteger juan = new BigInteger(64, new Random());
    	  BigInteger carlos = new BigInteger(64, new Random());
    	  System.out.println("Juan: " + juan);
    	  System.out.println("Carlos: " + carlos);
    	  juan = juan.xor(carlos);
    	  System.out.println("Xor: " + juan);
    	  juan = juan.xor(carlos);
    	  System.out.println("Final: " + juan);*/
      }
}
