import java.util.*;

public class ankBoard implements Comparable {
   public static final int BLACK = -1;
   public static final int WHITE = 1;
   public static final int NONE = 0;
   
   private long black;
   private long white;
   private static final long[][] mask = new long[8][8];
   static {
      for (int i = 0; i < 8; i++)
         for (int j = 0; j < 8; j++)
            mask[i][j] = 1L << (i * 8 + j);
   }
   
   private int score;
   private int numWhite;
   private int numBlack;
   //   private static final int weights[][] = new int[][]
   //   {{100,  -1, 5, 3, 3, 5,  -1, 100},
   //    {-1, -10, 1, 1, 1, 1, -10, -1},
   //    { 5,   1, 1, 1, 1, 1,   1, 5},
   //    { 3,   1, 1, 0, 0, 1,   1, 3},
   //    { 3,   1, 1, 0, 0, 1,   1, 3},
   //    { 5,   1, 1, 1, 1, 1,   1, 5},
   //    {-1, -10, 1, 1, 1, 1, -10, -1},
   //    {100,  -1, 5, 3, 3, 5,  -1, 100}
   //   };
   private static final int[][] weights = new int[][]
   {{500 ,  -25, 30 ,  10 ,   10,   30,  -25, 500},
    {-25,  -250, 0  ,  0  ,    0,    0,  -250, -25},
    {30  ,     0, 1  ,  2  ,    2,    1,     0, 30 },
    {10  ,     0, 2  ,  16 ,  16 ,  2  ,  0   , 10 },
    {10  ,     0, 2  ,  16 ,  16 ,  2  ,  0   , 10 },
    {30  ,     0, 1  ,  2  ,    2,    1,     0, 30 },
    {-25,  -250, 0  ,  0  ,    0,    0,  -250, -25},
    {500 ,  -25, 30 ,  10 ,   10,   30,  -25, 500}
   };
   //
   //   private static final int[][] weights = new int[][]
   //   {{500 ,  -150, 100 ,  100 ,   100,  100,  -150, 500},
   //    {-150,  -250, 0  ,  0  ,    0,    0,  -250, -150},
   //    {100 ,     0, 1  ,  2  ,    2,    1,     0, 100 },
   //    {100 ,     0, 2  ,  16 ,  16 ,  2  ,  0   , 100 },
   //    {100  ,     0, 2  ,  16 ,  16 ,  2  ,  0   , 100 },
   //    {100  ,     0, 1  ,  2  ,    2,    1,     0, 100 },
   //    {-150,  -250, 0  ,  0  ,    0,    0,  -250, -150},
   //    {500 ,  -150, 100 ,  100 ,   100,   100,  -150, 500}
   //   };
   public static final int EDGE_BONUS = 200;
   public static final int MOBILITY_FACTOR = 150;
   public static final int FRONTIER_SCORE = 35;
   
   private long blackMoves;
   private long whiteMoves;
   
   private ankBoard parent;
   private ankBoard[] children;
   private int[] move;
   private boolean opponentTurn;
   
   // Constructors ***********************************************************
   public ankBoard() {
      black = 0L;
      white = 0L;
      
      set(3, 3, WHITE);
      set(3, 4, BLACK);
      set(4, 3, BLACK);
      set(4, 4, WHITE);
      
      blackMoves |= (mask[2][3] | mask[3][2] | mask[4][5] | mask[5][4]);
      whiteMoves |= (mask[2][4] | mask[3][5] | mask[4][2] | mask[5][3]);
      
      score = 0;
   }
   
   public ankBoard(ankBoard b) {
      black = b.black;
      white = b.white;
      numWhite = b.numWhite;
      numBlack = b.numBlack;
      
      score = 0;
   }
   
   // Methods ****************************************************************
   public String toString() {
      String output = "";
      int temp;
      
      for (int i = 0; i < 8; i++) {
         for (int j = 0; j < 8; j++) {
            temp = get(i, j);
            if (temp == BLACK)
               output += "\t" + "Black";
            else if (temp == WHITE)
               output += "\t" + "White";
            else
               output += "\t" + "0";
         }
         output += "\n";
      }
      
      if (move != null)
         output += move[0] + ", " + move[1] + "\t Score: " + score
                 + (opponentTurn ? " oppo" : " myAI") + " just moved";
      else
         output += "\t Score: " + score
                 + (opponentTurn ? " oppo" : " myAI") + " just moved";
      
      output += "\nBLACK: " + numBlack + "\tWHITE: " + numWhite;
      return output;
   }
   
   public int compareTo(Object o) {
      int row = ((ankBoard) o).getMove()[0];
      int col = ((ankBoard) o).getMove()[1];
      
      if ((move[0] == 0 && move[1] == 0) || (move[0] == 7 && move[1] == 0)
      || (move[0] == 0 && move[1] == 7) || (move[0] == 7 && move[1] == 7))
         if ((row == 0 && col == 0) || (row == 7 && col == 0)
         || (row == 0 && col == 7) || (row == 7 && col == 7))
            return (Math.random() >= .5 ? -1 : 1);
         else
            return -1;
      if ((row == 0 && col == 0) || (row == 7 && col == 0)
      || (row == 0 && col == 7) || (row == 7 && col == 7))
         if ((move[0] == 0 && move[1] == 0) || (move[0] == 7 && move[1] == 0)
         || (move[0] == 0 && move[1] == 7) || (move[0] == 7 && move[1] == 7))
            return (Math.random() >= .5 ? -1 : 1);
         else
            return 1;
      
      if ((move[0] == 0 && (move[1] > 1 && move[1] < 6))
      || (move[1] == 0 && (move[0] > 1 && move[0] < 6))
      || (move[0] == 7 && (move[1] > 1 && move[1] < 6))
      || (move[1] == 7 && (move[0] > 1 && move[0] < 6)))
         if ((row == 0 && (col > 1 && col < 6))
         || (col == 0 && (row > 1 && row < 6))
         || (row == 7 && (col > 1 && col < 6))
         || (col == 7 && (row > 1 && row < 6)))
            return (Math.random() >= .5 ? -1 : 1);
         else
            return -1;
      if ((row == 0 && (col > 1 && col < 6))
      || (col == 0 && (row > 1 && row < 6))
      || (row == 7 && (col > 1 && col < 6))
      || (col == 7 && (row > 1 && row < 6)))
         if ((move[0] == 0 && (move[1] > 1 && move[1] < 6))
         || (move[1] == 0 && (move[0] > 1 && move[0] < 6))
         || (move[0] == 7 && (move[1] > 1 && move[1] < 6))
         || (move[1] == 7 && (move[0] > 1 && move[0] < 6)))
            return (Math.random() >= .5 ? -1 : 1);
         else
            return 1;
      
      if ((move[0] == 0 && (move[1] == 1 || move[1] == 6))
      || (move[1] == 0 && (move[0] == 1 || move[0] == 6))
      || (move[0] == 7 && (move[1] == 1 || move[1] == 6))
      || (move[1] == 7 && (move[0] == 1 || move[0] == 6)))
         if ((row == 0 && (col == 1 || col == 6))
         || (col == 0 && (row == 1 || row == 6))
         || (row == 7 && (col == 1 || col == 6))
         || (col == 7 && (row == 1 || row == 6)))
            return (Math.random() >= .5 ? -1 : 1);
         else
            return -1;
      if ((row == 0 && (col == 1 || col == 6))
      || (col == 0 && (row == 1 || row == 6))
      || (row == 7 && (col == 1 || col == 6))
      || (col == 7 && (row == 1 || row == 6)))
         if ((move[0] == 0 && (move[1] == 1 || move[1] == 6))
         || (move[1] == 0 && (move[0] == 1 || move[0] == 6))
         || (move[0] == 7 && (move[1] == 1 || move[1] == 6))
         || (move[1] == 7 && (move[0] == 1 || move[0] == 6)))
            return (Math.random() >= .5 ? -1 : 1);
         else
            return 1;
      
      if (row != 0 && row != 7 && col != 0 && col != 7)
         return (Math.random() > .5 ? -1 : 1);
      if (move[0] != 0 && move[0] != 7 && move[1] != 0 && move[1] != 7)
         return (Math.random() > .5 ? -1 : 1);
      
      return 1;
   }
   
   public void set(int row, int col, int color) {
      if (color == BLACK) {
         numBlack++;
         black = black | mask[row][col];
         white = (white | mask[row][col]) ^ mask[row][col];
      } else {
         numWhite++;
         white = white | mask[row][col];
         black = (black | mask[row][col]) ^ mask[row][col];
      }
   }
   
   public int get(int row, int col) {
      if ((black & mask[row][col]) == mask[row][col])
         return BLACK;
      else if ((white & mask[row][col]) == mask[row][col])
         return WHITE;
      else
         return NONE;
   }
   
   public int[] findMoves(int color) {
      int x, y;
      int[] moves = new int[64];
      int moveCtr = -1;
      
      //      if (color == BLACK) {
      //         for (int i = 0; i < 8; i++) {
      //            for (int j = 0; j < 8; j++) {
      //               if ((blackMoves & mask[i][j]) != 0) {
      //                  moves[++moveCtr] = i;
      //                  moves[++moveCtr] = j;
      //               }
      //            }
      //         }
      //      } else {
      //         for (int i = 0; i < 8; i++) {
      //            for (int j = 0; j < 8; j++) {
      //               if ((whiteMoves & mask[i][j]) != 0) {
      //                  moves[++moveCtr] = i;
      //                  moves[++moveCtr] = j;
      //               }
      //            }
      //         }
      //      }
      
      if (color == BLACK) {
         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
               x = i;
               y = j;
               if (((black | white) & mask[i][j]) == 0) {      // If no piece
                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0)
                           dy++;
                        x = i + dx;
                        y = j + dy;
                        // while on the board, and the piece at x, y is WHITE
                        while (x >= 0 && x < 8 && y >= 0 && y < 8
                                && ((white & mask[x][y]) != 0)) {
                           x += dx;
                           y += dy;
                        }
                        // if on the board and piece at x, y is BLACK
                        // and piece at (x-dx), (y-dy) is WHITE
                        if (x >= 0 && x < 8 && y >= 0 && y < 8
                                && ((black & mask[x][y]) != 0)
                                && ((white & mask[x - dx][y - dy]) != 0)) {
                           moves[++moveCtr] = i;
                           moves[++moveCtr] = j;
                           dx = dy = 2;
                        }
                     }
                  }
               }
            }
         }
      } else {
         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
               x = i;
               y = j;
               if (((black | white) & mask[i][j]) == 0) {      // If no piece
                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0)
                           dy++;
                        x = i + dx;
                        y = j + dy;
                        // while on the board, and the piece at x, y is BLACK
                        while (x >= 0 && x < 8 && y >= 0 && y < 8
                                && ((black & mask[x][y]) != 0)) {
                           x += dx;
                           y += dy;
                        }
                        // if on the board and piece at x, y is WHITE
                        // and piece at (x-dx), (y-dy) is BLACK
                        if (x >= 0 && x < 8 && y >= 0 && y < 8
                                && ((white & mask[x][y]) != 0)
                                && ((black & mask[x - dx][y - dy]) != 0)) {
                           moves[++moveCtr] = i;
                           moves[++moveCtr] = j;
                           dx = dy = 2;
                        }
                     }
                  }
               }
            }
         }
      }
      // Store the number of moves and mobility of this array
      moves[63] = moveCtr + 1;
      
      return moves;
   }
   
   public boolean isLegal(int row, int col, int color) {
      int x = row;
      int y = col;
      if (((black | white) & mask[row][col]) == 0) {      // If no piece
         for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
               if (dx == 0 && dy == 0)
                  dy++;
               x = row + dx;
               y = col + dy;
               while (x >= 0 && x < 8 && y >= 0 && y < 8
                       && (((white | black) & mask[x][y]) == 0 ? NONE :
                          ((black & mask[x][y]) == 0 ? WHITE : BLACK)) == -color) {
                  x += dx;
                  y += dy;
               }
               
               if (x >= 0 && x < 8 && y >= 0 && y < 8
                       && (((white | black) & mask[x][y]) == 0 ? NONE :
                          ((black & mask[x][y]) == 0 ? WHITE : BLACK)) == color
                       && (((white | black) & mask[x - dx][y - dy]) == 0 ? NONE :
                          ((black & mask[x - dx][y - dy]) == 0 ? WHITE : BLACK)) == -color) {
                  return true;
               }
            }
         }
      }
      
      return false;
   }
   
   public void update(int row, int col, int color) {
      int x, y, dx, dy;
      int flippedCtr = 0;
      
      if (color == BLACK) {
         black = (black | mask[row][col]);
         white = ((white | mask[row][col]) ^ mask[row][col]);
         numBlack++;
      } else {
         white = (white | mask[row][col]);
         black = ((black | mask[row][col]) ^ mask[row][col]);
         numWhite++;
      }
      
      for (dx = -1; dx <= 1; dx++) {
         for (dy = -1; dy <= 1; dy++) {
            if (dx == 0 && dy == 0)
               dy++;
            x = row + dx;
            y = col + dy;
            while (x >= 0 && x < 8 && y >=0 && y < 8
                    && ((black & mask[x][y]) == 0 ? ((white & mask[x][y]) == 0 ? NONE : WHITE) : BLACK) == -color) {
               x += dx;
               y += dy;
            }
            
            if (x >= 0 && x < 8 && y >=0 && y < 8
                    && ((black & mask[x][y]) == 0 ? ((white & mask[x][y]) == 0 ? NONE : WHITE) : BLACK) == color) {
               x -= dx;
               y -= dy;
               while (x != row || y != col) {
                  if (color == BLACK) {
                     black = (black | mask[x][y]);
                     white = ((white | mask[x][y]) ^ mask[x][y]);
                     numBlack++;
                     numWhite--;
                  } else {
                     white = (white | mask[x][y]);
                     black = ((black | mask[x][y]) ^ mask[x][y]);
                     numBlack--;
                     numWhite++;
                  }
                  x -= dx;
                  y -= dy;
               }
            }
         }
      }
      
      //      blackMoves = whiteMoves = 0;
      //      for (int i = 0; i < 8; i++) {
      //         for (int j = 0; j < 8; j++) {
      //            x = i;
      //            y = j;
      //            if (((black | white) & mask[i][j]) == 0) {      // If no piece
      //               // find BLACK moves
      //               for (dx = -1; dx <= 1; dx++) {
      //                  for (dy = -1; dy <= 1; dy++) {
      //                     if (dx == 0 && dy == 0)
      //                        dy++;
      //                     x = i + dx;
      //                     y = j + dy;
      //                     // while on the board, and the piece at x, y is WHITE
      //                     while (x >= 0 && x < 8 && y >= 0 && y < 8
      //                           && ((white & mask[x][y]) != 0)) {
      //                        x += dx;
      //                        y += dy;
      //                     }
      //                     // if on the board and piece at x, y is BLACK
      //                     // and piece at (x-dx), (y-dy) is WHITE
      //                     if (x >= 0 && x < 8 && y >= 0 && y < 8
      //                           && ((black & mask[x][y]) != 0)
      //                           && ((white & mask[x - dx][y - dy]) != 0)) {
      //                        blackMoves |= mask[i][j];
      //                        dx = dy = 2;
      //                     }
      //                  }
      //               }
      //
      //               // find WHITE moves
      //               for (dx = -1; dx <= 1; dx++) {
      //                  for (dy = -1; dy <= 1; dy++) {
      //                     if (dx == 0 && dy == 0)
      //                        dy++;
      //                     x = i + dx;
      //                     y = j + dy;
      //                     // while on the board, and the piece at x, y is BLACK
      //                     while (x >= 0 && x < 8 && y >= 0 && y < 8
      //                           && ((black & mask[x][y]) != 0)) {
      //                        x += dx;
      //                        y += dy;
      //                     }
      //                     // if on the board and piece at x, y is WHITE
      //                     // and piece at (x-dx), (y-dy) is BLACK
      //                     if (x >= 0 && x < 8 && y >= 0 && y < 8
      //                           && ((white & mask[x][y]) != 0)
      //                           && ((black & mask[x - dx][y - dy]) != 0)) {
      //                        whiteMoves |= mask[i][j];
      //                        dx = dy = 2;
      //                     }
      //                  }
      //               }
      //            }
      //         }
      //      }
   }
   
   /**
    * Mobility is computed by taking the difference between moves available for
    * each player.  Moves available is defined as the number of spaces (possibly
    * overlapping) that one side's set of pieces can move into. Ex:
    *    0  0  0  0  0
    *    0  B  0  0  0
    *    0  B  B  0  0
    *    0  B  W  B  0
    *    0  0  0  0  0
    * Black has a mobility of 2 while white's mobility is 4.
    * computeMobility(BLACK) would return -2.
    *
    * Another:
    *    0  0  0  0  0
    *    0  B  0  B  0
    *    0  W  W  W  0
    *    0  0  0  0  0
    * Black's mobility is 4 and white's mobility is also 4
    */
   //   public int computeMobility(int color) {
   //      int x, y;
   //      int whiteMobility = 0;
   //      int blackMobility = 0;
   //      int whiteFrontier = 0;
   //      int blackFrontier = 0;
   //
   //      if (numBlack + numWhite >= 32) {
   //         for (int i = 0; i < 8; i++) {
   //            for (int j = 0; j < 8; j++) {
   //               x = i;
   //               y = j;
   //               if (((black | white) & mask[x][y]) == 0) {      // If no piece
   //                  for (int dx = -1; dx <= 1; dx++) {
   //                     for (int dy = -1; dy <= 1; dy++) {
   //                        if (dx == 0 && dy == 0)
   //                           dy++;
   //                        x = i + dx;
   //                        y = j + dy;
   //
   //                        // check for a frontier disk
   //                        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
   //                           if ((white & mask[x][y]) != 0)
   //                              whiteFrontier++;
   //                           else if ((black & mask[x][y]) != 0)
   //                              blackFrontier++;
   //                        }
   //
   //                        // while on the board and (x,y) is WHITE
   //                        while (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && ((white & mask[x][y]) != 0)) {
   //                           x += dx;
   //                           y += dy;
   //                        }
   //
   //                        // if on the board and piece at x, y is BLACK
   //                        // and piece at (x-dx), (y-dy) is WHITE
   //                        if (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && ((black & mask[x][y]) != 0)
   //                                && ((white & mask[x - dx][y - dy]) != 0)) {
   //                           blackMobility++;
   //                        }
   //
   //                        // Now compute other color's mobility
   //                        x = i + dx;
   //                        y = j + dy;
   //                        while (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && ((black & mask[x][y]) != 0)) {
   //                           x += dx;
   //                           y += dy;
   //                        }
   //
   //                        if (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && ((white & mask[x][y]) != 0)
   //                                && ((black & mask[x - dx][y - dy]) != 0)) {
   //                           whiteMobility++;
   //                        }
   //                     }
   //                  }
   //               }
   //            }
   //         }
   //      } else {
   //         for (int i = 0; i < 8; i++) {
   //            for (int j = 0; j < 8; j++) {
   //               x = i;
   //               y = j;
   //               if ((black & mask[x][y]) != 0) {          // If it's Black
   //                  for (int dx = -1; dx <= 1; dx++) {
   //                     for (int dy = -1; dy <= 1; dy++) {
   //                        if (dx == 0 && dy == 0)
   //                           dy++;
   //                        x = i + dx;
   //                        y = j + dy;
   //
   //                        // check for frontier disk
   //                        if ((x >= 0 && x < 8 && y >= 0 && y < 8)
   //                        && (((black | white) & mask[x][y]) == 0))
   //                           blackFrontier++;
   //
   //                        while (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && (white & mask[x][y]) != 0) {
   //                           x += dx;
   //                           y += dy;
   //                        }
   //
   //                        if (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && (x != i + dx || y != j + dy)
   //                                && (((black | white) & mask[x][y]) == 0)) {
   //                           blackMobility++;
   //                        }
   //                     }
   //                  }
   //               } else if ((white & mask[x][y]) != 0) {   // If it's White
   //                  for (int dx = -1; dx <= 1; dx++) {
   //                     for (int dy = -1; dy <= 1; dy++) {
   //                        if (dx == 0 && dy == 0)
   //                           dy++;
   //                        x = i + dx;
   //                        y = j + dy;
   //
   //                        // check for frontier disk
   //                        if ((x >= 0 && x < 8 && y >= 0 && y < 8)
   //                        && (((black | white) & mask[x][y]) == 0))
   //                           whiteFrontier++;
   //
   //                        while (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && (black & mask[x][y]) != 0) {
   //                           x += dx;
   //                           y += dy;
   //                        }
   //
   //                        if (x >= 0 && x < 8 && y >= 0 && y < 8
   //                                && (x != i + dx || y != j + dy)
   //                                && (((black | white) & mask[x][y]) == 0)) {
   //                           whiteMobility++;
   //                        }
   //                     }
   //                  }
   //               }
   //            }
   //         }
   //      }
   //
   //      return (color == BLACK) ?
   //         (blackMobility - whiteMobility) * MOBILITY_FACTOR + (whiteFrontier - blackFrontier) * FRONTIER_SCORE
   //              : (whiteMobility - blackMobility) * MOBILITY_FACTOR + (blackFrontier - whiteFrontier) * FRONTIER_SCORE;
   //   }
   
   public int computeMobility(int color) {
      int x, y;
      int whiteFrontier = 0;
      int blackFrontier = 0;
      
      if (numBlack + numWhite >= 32) {
         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
               x = i;
               y = j;
               if (((black | white) & mask[x][y]) == 0) {      // If no piece
                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0)
                           dy++;
                        x = i + dx;
                        y = j + dy;
                        
                        // check for a frontier disk
                        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                           if ((white & mask[x][y]) != 0)
                              whiteFrontier++;
                           else if ((black & mask[x][y]) != 0)
                              blackFrontier++;
                        }
                     }
                  }
               }
            }
         }
      } else {
         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
               x = i;
               y = j;
               if ((black & mask[x][y]) != 0) {          // If it's Black
                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0)
                           dy++;
                        x = i + dx;
                        y = j + dy;
                        
                        // check for frontier disk
                        if ((x >= 0 && x < 8 && y >= 0 && y < 8)
                        && (((black | white) & mask[x][y]) == 0))
                           blackFrontier++;
                     }
                  }
               } else if ((white & mask[x][y]) != 0) {   // If it's White
                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0)
                           dy++;
                        x = i + dx;
                        y = j + dy;
                        
                        // check for frontier disk
                        if ((x >= 0 && x < 8 && y >= 0 && y < 8)
                        && (((black | white) & mask[x][y]) == 0))
                           whiteFrontier++;
                     }
                  }
               }
            }
         }
      }
      
      return (color == BLACK) ?
         (whiteFrontier - blackFrontier) * FRONTIER_SCORE * 5
              : (blackFrontier - whiteFrontier) * FRONTIER_SCORE * 5;
   }
   
   public int evalBoard(int color) {
      // A win/loss scenario
      if (numBlack == 0) {
         return (color == BLACK ? -9999999 : 9999999);
      } else if (numWhite == 0) {
         return (color == WHITE ? -9999999 : 9999999);
      }
      
      int piece;
      int tempPiece;
      score = 0;
      
      // Compute simple board weights
      for (int i = 0; i < 8; i++) {
         for (int j = 0; j < 8; j++) {
            piece = ((white | black) & mask[i][j]) == 0 ? NONE :
               ((black & mask[i][j]) == 0 ? WHITE : BLACK);
            if (piece != NONE) {
               score += (piece == color) ? weights[i][j] : -weights[i][j];
               
               //               if (numBlack + numWhite <= 32) {
               //                  for (int dx = -1; dx <= 1; dx++) {
               //                     for (int dy = -1; dy <= 1; dy++) {
               //                        if (dx == 0 && dy == 0)
               //                           dy++;
               //
               //                        if (i + dx >= 0 && i + dx < 8 && j + dy >= 0 && j + dy < 8) {
               //                           tempPiece = ((white | black) & mask[i][j]) == 0 ? NONE :
               //                              ((black & mask[i][j]) == 0 ? WHITE : BLACK);
               //                           if (tempPiece == NONE) {
               //                              score += (piece == color) ? -FRONTIER_SCORE : FRONTIER_SCORE;
               //                              foundFrontier = true;
               //                              break;
               //                           }
               //                        }
               //                     }
               //                     if (foundFrontier)
               //                        break;
               //                  }
               //               }
            }
         }
      }
      
      // Factor in edge (stability) effects
      int corner;
      int x, y, dx, dy;
      
      for (int i = 0; i < 8; i += 7) {
         for (int j = 0; j < 8; j += 7) {
            corner = ((white | black) & mask[i][j]) == 0 ? NONE :
               ((black & mask[i][j]) == 0 ? WHITE : BLACK);
            dx = (i == 0) ? 1 : -1;
            dy = (j == 0) ? 1 : -1;
            x = i;
            y = j;
            
            if (corner == color) {
               while (((black & mask[i][y += dy]) == 0 ? WHITE : BLACK) == color && y < 7 && y > 0)
                  score = score - weights[i][y] + EDGE_BONUS;
               while (((black & mask[x += dx][j]) == 0 ? WHITE : BLACK) == color && x < 7 && x > 0)
                  score = score - weights[x][j] + EDGE_BONUS;
               if (((black & mask[i + dx][j + dy]) == 0 ? WHITE : BLACK) == color)
                  score -= weights[i + dx][j + dy];
            } else if (corner == -color) {
               while (((black & mask[i][y += dy]) == 0 ? WHITE : BLACK) == -color && y < 7 && y > 0)
                  score = score + weights[i][y] - EDGE_BONUS;
               while (((black & mask[x += dx][j]) == 0 ? WHITE : BLACK) == -color && x < 7 && x > 0)
                  score = score + weights[x][j] - EDGE_BONUS;
               if (((black & mask[i + dx][j + dy]) == 0 ? WHITE : BLACK) == -color)
                  score += weights[i + dx][j + dy];
            }
         }
      }
      
      // return score plus a mobility score.
      return score + computeMobility(color);
   }
   
   public int getScore() {
      return score;
   }
   
   public void setScore(int score) {
      this.score = score;
   }
   
   public long[] getBoard() {
      return new long[] {black, white};
   }
   
   public ankBoard getParent() {
      return parent;
   }
   
   public void setParent(ankBoard parent) {
      this.parent = parent;
   }
   
   public ankBoard[] getChildren() {
      return children;
   }
   
   public void setChildren(ankBoard[] children) {
      this.children = children;
   }
   
   public void setMove(int[] move) {
      this.move = move;
   }
   
   public int[] getMove() {
      return move;
   }
   
   public void setOppoTurn(boolean turn) {
      opponentTurn = turn;
   }
   
   public boolean isOppoTurn() {
      return opponentTurn;
   }
   
   public long getBlack() {
      return black;
   }
   
   public long getWhite() {
      return white;
   }
   
   public int getNumBlack() {
      return numBlack;
   }
   
   public int getNumWhite() {
      return numWhite;
   }
   
   public long getWhiteMoves() {
      return whiteMoves;
   }
   
   public long getBlackMoves() {
      return blackMoves;
   }
   
   public void reset(long black, long white, int numBlack, int numWhite) {
      this.black = black;
      this.white = white;
      this.whiteMoves = whiteMoves;
      this.blackMoves = blackMoves;
      this.numBlack = numBlack;
      this.numWhite = numWhite;
   }
   
   public static void main(String[] args) {
      System.out.println((-10 >> 1));
   }
}
