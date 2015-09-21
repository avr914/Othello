import java.util.*;

public class DeepKwok implements OthelloPlayer {
   private int myColor;
   
   private ankBoard boardTree;
   private ankBoard gameBoard;
   private static final int START_DEPTH = 6;
   private int currentDepth;
   private static final int END_DEPTH = 18;
   private int endSearchAmt;
   private boolean search;
   private static final long UNLIMITED_LIMIT = 45000;  // if given unlimited time
      
   private TranspositionTable tTable;
   private int lookups;
   private LinkedList scoreList;
   
   // Opening book data members
   private ankOpeningTree book;
   private int symmetryType;
   private boolean bookExhausted;
   
   private long numMoves;
   private long mostMoves;
   private long totalMoves;
   private double totalTime;
   private long startTime;
   private int movesLeft;
   private long timeLimit;
   
   private static final double TIME_FACTOR = 10;
   
   public DeepKwok() {
      
   }
   
   // Methods ****************************************************************
   public void init(OthelloSide side) {
      myColor = (side.toString().equals("Black")) ? ankBoard.BLACK : ankBoard.WHITE;
      gameBoard = new ankBoard();
      movesLeft = 60;
      tTable = new TranspositionTable();
      tTable.entries = 0;
      scoreList = new LinkedList();
      
      book = new ankOpeningTree();
      ankBookParser parse = new ankBookParser(book, "ankBook.txt");
      book.searchScores(ankBoard.WHITE, book);
      bookExhausted = false;
      if (myColor == ankBoard.BLACK)
         symmetryType = ankOpeningTree.C4;
   }
   
   public Move doMove(Move opponentsMove, long timeLeft) {
      startTime = System.currentTimeMillis();
      timeLeft = Math.max(timeLeft - 10000, 250);  // Some leeway
      
      numMoves = 0;
      lookups = 0;
      tTable.cleanUp(66 - movesLeft);
      search = true;
      
      // Make opponent's move (if any)
      if (opponentsMove != null) {
         gameBoard.update(opponentsMove.getY(), opponentsMove.getX(), -myColor);
         movesLeft--;
      }
      
      try {
         if (bookExhausted == false) {
            if (myColor == ankBoard.BLACK) {
               if (movesLeft == 60) {
                  System.out.println("Using book, returning " + new Move(2, 3));
                  movesLeft--;
                  Move m = new Move(2, 3);
                  gameBoard.update(m.getY(), m.getX(), myColor);
                  return new Move(2, 3);
               }

               book = book.getOpening(opponentsMove);
               if (book == null)
                  throw new Exception();
               
               Move m = book.getNextMove();
               if (m == null)
                  throw new Exception();
               
               book = book.getOpening(m);
               
               System.out.println("Using book, returning " + m);
               movesLeft--;
               gameBoard.update(m.getY(), m.getX(), myColor);
               return m;
            } else {
               if (movesLeft == 59) {
                  if (opponentsMove.equals(new Move(2, 3)))
                     symmetryType = ankOpeningTree.C4;
                  else if (opponentsMove.equals(new Move(3, 2)))
                     symmetryType = ankOpeningTree.D3;
                  else if (opponentsMove.equals(new Move(5, 4)))
                     symmetryType = ankOpeningTree.F5;
                  else if (opponentsMove.equals(new Move(4, 5)))
                     symmetryType = ankOpeningTree.E6;
               }
               
               if (movesLeft < 59)
                  book = book.getOpening(ankOpeningTree.transform(opponentsMove, symmetryType));
               
               if (book == null)
                  throw new Exception();
               
               Move m = book.getNextMove();
               if (m == null)
                  throw new Exception();
               
               book = book.getOpening(m);
               
               m = ankOpeningTree.transform(m, symmetryType);
               System.out.println("Using book, returning " + m);
               movesLeft--;
               gameBoard.update(m.getY(), m.getX(), myColor);
               return m;
            }
         }
      } catch (Exception e) {
         System.out.println("Book is exhausted");
         bookExhausted = true;
      }
      
      if (timeLeft <= 0) { // Unlimited time
	  System.out.println("unlimited time! yay!!!");
         if (movesLeft > END_DEPTH)
            timeLimit = startTime + UNLIMITED_LIMIT;
         else
            timeLimit = startTime + 350000;
      } else if (movesLeft > END_DEPTH) {
         timeLimit = startTime + (long) (2 * timeLeft / (movesLeft - 8));
      } else {
         double timeFactor = 0.0;
         double deltaT = 0.0;
         
         if (movesLeft > 8) {
            timeFactor = Math.pow(timeLeft / 15.0, 2.0 / (movesLeft - 8));
            
            deltaT = 15 * (Math.pow(timeFactor, movesLeft / 2.0 - 4) - Math.pow(
                    timeFactor, (movesLeft - 2) / 2.0 - 4));
            timeLimit = (long) Math.min(startTime + deltaT,
                    startTime + timeLeft - 25);
         } else
            timeLimit = (long) Math.min((startTime + timeLeft - 25 * (movesLeft >> 1)), startTime + timeLeft - 25);
         
         System.out.println("timeLimit = " + (timeLimit - startTime) + "\ntimeLeft = " + timeLeft);
      }
      
      // top node
      boardTree = new ankBoard(gameBoard);
      
      int[] moves = gameBoard.findMoves(myColor);
      int movesLength = moves[63];
      numMoves += movesLength;
      LinkedList children = new LinkedList();
      
      // If no moves possible, return null
      if (movesLength == 0)
         return null;
      else {
         // Construct the first set of children
         ankBoard next;
         TreeSet sorter = new TreeSet();
         
         for (int i = 0; i < movesLength; i += 2) {
            next = new ankBoard(gameBoard);
            next.update(moves[i], moves[i + 1], myColor);
            next.setMove(new int[] {moves[i], moves[i + 1]});
            
            sorter.add(next);
         }
         children = new LinkedList(sorter);
      }
      
      boardTree.evalBoard(myColor);
      //System.out.println(boardTree);
      
      int[] bestMove = ((ankBoard) children.getFirst()).getMove();
      
      int bestScore = -9999999;
      int[] scorePair;
      int guess = -9999999;
      int tempScore;
      
      long currentTime;
      double elapsed = 0.0;
      
      // Search through the set of children for the best board
      currentDepth = START_DEPTH;
      boolean endSearch = false;
      int moveIndex = 0;
      ankBoard temp;
      Iterator iter;
      try {
         long time = System.currentTimeMillis();
         long delT;
         while (endSearch == false) {
            guess = bestScore = -9999999;
            if (movesLeft <= END_DEPTH) {
               endSearch = true;
	       if (movesLeft > END_DEPTH - 2)
                  guess = bestScore = endSearchAmt = -1;
	       else 
         	  guess = bestScore = endSearchAmt - 5;
            } else {
               // check if there's enough time to go to next depth
               delT = System.currentTimeMillis() - time;
               if (delT * TIME_FACTOR + System.currentTimeMillis() > timeLimit)
                  break;
               
               time = System.currentTimeMillis();
               
               while (endSearch == false) {
                  if (scoreList.size() != 0) {
                     scorePair = (int[]) scoreList.getFirst();
                     if (scorePair[0] < movesLeft)
                        scoreList.removeFirst();
                     else if (scorePair[0] == movesLeft) {
                        guess = bestScore = scorePair[1];
                        scoreList.removeFirst();
                        endSearch = true;
                     } else
                        endSearch = true;
                  } else
                     endSearch = true;
               }
               
               endSearch = false;
            }
            
            int startDepth = (movesLeft <= END_DEPTH) ? movesLeft : currentDepth;
	    startDepth = (startDepth <= movesLeft) ? startDepth : movesLeft;
            iter = children.iterator();
            System.out.println("Going to Depth " + startDepth);
            for (int i = 0; i < children.size(); i++) {
               temp = (ankBoard) iter.next();
               tempScore = minimaxAB(temp, -myColor, bestScore, 9999999, startDepth - 1);
               System.out.println(tempScore + "\t" + temp.getMove()[0] + ", " + temp.getMove()[1]);
               if (tempScore > bestScore) {
                  bestScore = tempScore;
                  bestMove = temp.getMove();
                  moveIndex = i;
               }
	       if (bestScore > endSearchAmt && movesLeft <= END_DEPTH)
		   endSearchAmt = bestScore;
            }
            
            // if guess was too high, research!
            if (bestScore == guess) {
               System.out.println("uh oh...");
               bestScore = -10000000;
               startDepth = (movesLeft <= END_DEPTH) ? movesLeft : currentDepth;
               iter = children.iterator();
               for (int i = 0; i < children.size(); i++) {
                  temp = (ankBoard) iter.next();
                  tempScore = minimaxAB(temp, -myColor, bestScore, 9999999, startDepth - 1);
                  System.out.println(tempScore + "\t" + temp.getMove()[0] + ", " + temp.getMove()[1]);
                  if (tempScore > bestScore) {
                     bestScore = tempScore;
                     bestMove = temp.getMove();
                     moveIndex = i;
                  }
               }
            }
            // Move the current best move to the top of search list to
            // facilitate the alpha-beta search
            children.addFirst(children.remove(moveIndex));
            scoreList.add(new int[] { movesLeft - 1, bestScore - 1 });
            elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
            currentDepth += 2;
         }
      } catch (OutOfTimeException e) {
         System.out.println("INCOMPLETE SEARCH");
      }
      
      elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
      totalTime += elapsed;
      System.out.println("Elapsed Time = " + elapsed);
      System.out.println("Number Moves Calculated = " + numMoves);
      totalMoves += numMoves;
      if (numMoves > mostMoves) {
         mostMoves = numMoves;
      }
      
      System.out.println("Successful lookups = " + lookups);
      System.out.println("Entries = " + tTable.entries);
      System.out.println("Most Moves = " + mostMoves);
      System.out.println("Avg moves/sec = " + (totalMoves / totalTime));
      System.out.println("DeepKwok picks " + bestMove[0] + ", " + bestMove[1]);
      
      // Update my move here
      gameBoard.update(bestMove[0], bestMove[1], myColor);
      movesLeft--;
      
      return new Move(bestMove[1], bestMove[0]);
   }
   
   /**
    * From http://mainline.brynmawr.edu/Courses/cs372/fall2000/AB.html.  Also,
    * the board uses a different evaluation scheme for the end game (last 18
    * moves) since it is possible to completely search until the last move, with
    * a worst-case scenario of 18! moves.
    */
   public int minimaxAB(ankBoard current, int color, int alpha, int beta,
           int depth) throws OutOfTimeException {
      
      int numBlack = current.getNumBlack();
      int numWhite = current.getNumWhite();
      if (depth > 0) {
         long black = current.getBlack();
         long white = current.getWhite();
         int move;
         int firstMoveX = 0;
         int firstMoveY = 0;
         int bestMoveX, bestMoveY;
         bestMoveX = bestMoveY = 0;
         
         if ((move = tTable.getEntry(white, black)) != TranspositionTable.NO_MOVE) {
            firstMoveX = bestMoveX = move >> 3;
            firstMoveY = bestMoveY = move & 7;
         }
         
         // Check if I'm out of time
         if (System.currentTimeMillis() > timeLimit)
            throw new OutOfTimeException();
         
         int[] moves = current.findMoves(color);
         int movesLength = moves[63];
         
         int val;
         
         if (color == -myColor) {   // if player is min
            if (movesLength == 0) { // if there are no moves
               val = minimaxAB(current, -color, alpha, beta, depth - 1);
               
               beta = (val < beta) ? val : beta;
               numMoves++;
               
               return beta;
            }
            
            if (current.isLegal(firstMoveX, firstMoveY, color)) {
               current.update(firstMoveX, firstMoveY, color);
               val = minimaxAB(current, -color, alpha, beta, depth - 1);
               beta = (val < beta) ? val : beta;
               current.reset(black, white, numBlack, numWhite);
               lookups++;
               numMoves++;
            }
            
            for (int i = 0; i < movesLength && alpha < beta; i += 2) {
               if (moves[i] == firstMoveX && moves[i + 1] == firstMoveY)
                  continue;
               
               current.update(moves[i], moves[i + 1], color);
               val = minimaxAB(current, -color, alpha, beta, depth - 1);
               
               if (val < beta) {
                  beta = val;
                  bestMoveX = moves[i];
                  bestMoveY = moves[i + 1];
               }
               
               // reset the board
               current.reset(black, white, numBlack, numWhite);
               numMoves++;
            }
            
            if (bestMoveX != firstMoveX && bestMoveY != firstMoveY)
               tTable.addEntry(white, black, bestMoveX, bestMoveY, numBlack + numWhite, 64 - movesLeft);
            
            return beta;
         } else {    // if player is max
            if (movesLength == 0) { // If there are no moves
               val = minimaxAB(current, -color, alpha, beta, depth - 1);
               
               alpha = (val > alpha) ? val : alpha;
               numMoves++;
               
               return alpha;
            }
            
            if (current.isLegal(firstMoveX, firstMoveY, color)) {
               current.update(firstMoveX, firstMoveY, color);
               val = minimaxAB(current, -color, alpha, beta, depth - 1);
               alpha = (val > alpha) ? val : alpha;
               current.reset(black, white, numBlack, numWhite);
               lookups++;
               numMoves++;
            }
            
            for (int i = 0; i < movesLength && alpha < beta; i += 2) {
               if (moves[i] == firstMoveX && moves[i + 1] == firstMoveY)
                  continue;
               
               current.update(moves[i], moves[i + 1], color);
               val = minimaxAB(current, -color, alpha, beta, depth - 1);
               
               if (val > alpha) {
                  alpha = val;
                  bestMoveX = moves[i];
                  bestMoveY = moves[i + 1];
               }
               
               // Reset the board
               current.reset(black, white, numBlack, numWhite);
               numMoves++;
            }
            
            if (bestMoveX != firstMoveX && bestMoveY != firstMoveY)
               tTable.addEntry(white, black, bestMoveX, bestMoveY, numBlack + numWhite, 64 - movesLeft);
            
            return alpha;
         }
      } else {
         // Maximize number of pieces for end game.
         if (movesLeft <= END_DEPTH) {
            return (myColor == ankBoard.BLACK ? numBlack - numWhite : numWhite - numBlack);
         } else {
            return current.evalBoard(myColor);
         }
      }
   }
   
   private class OutOfTimeException extends Exception {
      public OutOfTimeException() {
         super();
      }
   }
}
