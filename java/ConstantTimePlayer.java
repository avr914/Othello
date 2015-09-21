import java.util.*;

/** This class implements a simple  Othello AI. It calculates
 *  a list of possible moves, then picks one depending on a set of rules.
 *  This player is mainly for testing purposes.
 * <p>
 *  Moves are ranked by the rules, and then one of the top five moves
 *  is chosen randomly.
 *  It implements {@link OthelloPlayer}.
 *
 *  @author Robert Morell
 */

public class ConstantTimePlayer implements OthelloPlayer {
    
    private OthelloSide side;
    private OthelloSide opponentSide;

    // Random generator
    private Random gen = new Random();

    // AI's copy of the board.
    private OthelloBoard board = new OthelloBoard();
    
    /** Initialize the AI. Doesn't do much other than set the side. */
    public void init(OthelloSide side) {
        this.side = side;
        if (side == OthelloSide.BLACK)
            opponentSide = OthelloSide.WHITE;
        else
            opponentSide = OthelloSide.BLACK;
    }
    
    /** Returns the next move.
     * Takes in the opponents' move.
     */
    public Move doMove(Move opponentsMove, long millisLeft) {
      // try {
      // Thread.sleep(2000);
      // } catch(Exception e) {}
        
        // Reflect oppenent's move on our copy of board
        if (opponentsMove != null) 
            board.move(opponentsMove, opponentSide);
        
        // Get our possible moves.
        LinkedList moveList = getMoveList(board, side);
        
        // Return the move that results in the highest score.
        if (moveList.size() != 0) {
	    OthelloBoard newBoard;
	    Iterator i = moveList.listIterator(0);
	    Move bestmove = null, move;
       TreeSet moves = new TreeSet();
	    while (i.hasNext()) {
		int currentscore;
		move = (Move) i.next();
		newBoard = board.copy();
		newBoard.move(move, side);
		currentscore = evaluateBoard(newBoard, side);
		moves.add(new ScoredMove(currentscore, move));
	    }
	    System.out.println("Found " + moves.size() + " legal moves.");

	    // Determine how many moves have the same score as the
	    // best one.
	    int count = 1;
	    i = moves.iterator();
	    int bestscore = ((ScoredMove)i.next()).score;
	    while(i.hasNext())
	    {
		if(((ScoredMove)i.next()).score != bestscore)
			break;
	    	count++;
	    }
	    System.out.println("Found " + count + " best moves.");

	    // Choose a random move.
	    int moveNum = gen.nextInt(count);
	    System.out.println("Choosing move " + moveNum);
	    i = moves.iterator();
	    for(int c = 0; c <= moveNum; c++)
	    	bestmove = ((ScoredMove)i.next()).move;
	    
	    System.out.println("Picked: " + bestmove);
            board.move(bestmove, side);
            return bestmove;
        }
        else {
            // PASS; no moves available.
            System.out.println("PASS");
            return null;
        }
    }

    /* Returns a score for a possible board.  The higher the score, the
     * better the situation.
     */
    private int evaluateBoard(OthelloBoard board, OthelloSide side) {
	int score = 0;
	for (int x = 0; x <= 7; x++)
	    for (int y = 0; y <= 7; y++) {
		if (board.get(side, x, y))
		    score += evaluatePiece(board, side, x, y);
		else if (board.get(opponentSide, x, y))
		    score -= evaluatePiece(board, opponentSide, x, y);
	    }
	return score;
    }

    private int evaluatePiece(OthelloBoard board, OthelloSide side, int x, int y) {
	// 1 is the standard score.
	int score = 1;

	// Add 30 for a corner piece.
	if ((x == 0 || x == 7) && (y == 0 || y ==7))
	    score += 40;

	// Add 5 for a side piece.
	if (x == 0 || x == 7 || y == 0 || y == 7)
	    score += 10;

	// Subtract 1 for the adjacent-to-sides.
	if (x == 1 || x == 6 || y == 1 || y == 6) {
	    score -= 1;

	    // Subtract 5 if you are adjacent to a corner but not in that corner.
	    if ((x == 1 && y == 1) && !board.get(side, 0, 0))
		score -= 5;
	    if ((x == 1 && y == 6) && !board.get(side, 0, 7))
		score -= 5;
	    if ((x == 6 && y == 1) && !board.get(side, 7, 0))
		score -= 5;
	    if ((x == 6 && y == 6) && !board.get(side, 7, 7))
		score -= 5;
	}

	return score;
    }
    
    // Returns a list of possible moves.
    private LinkedList getMoveList(OthelloBoard board, OthelloSide side) {
        Move move;
        LinkedList moveList = new LinkedList();
        for (int i = 0; i <= 7; i++)
            for (int j = 0; j <= 7; j++) {
                move = new Move(i,j);
                if (board.checkMove(move, side)) {
                    moveList.add(move);
                }
            }
        return moveList;
    }
}

class ScoredMove implements Comparable
{
   public final int score;
   public final Move move;

   public ScoredMove(final int score, final Move move)
   {
      this.score = score;
      this.move = move;
   }

   public int compareTo(Object o)
   {
      // This had better be a ScoredMove
      ScoredMove m = (ScoredMove)o;

      if(score < m.score)
         return 1;
      else if(score > m.score)
         return -1;
      else if(this.hashCode() > o.hashCode())
         return 1;
      else
         return -1;
   }
}
