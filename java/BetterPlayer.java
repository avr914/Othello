/* Aaron: This is just a renamed RecPlayer3 */

import java.util.*;

/** This class implements a somewhat complex Othello AI. */

public class BetterPlayer implements OthelloPlayer {
    
    private int RecLevel()
    {
      return 3;
    }

    // The weight to apply to the score recurse computes.
    final static float RECURSION_WEIGHT = 0.85f;
    
    private OthelloSide side;
    private OthelloSide opponentSide;
    
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
        
        // Reflect oppenent's move on our copy of board
        if(opponentsMove != null)
           board.move(opponentsMove, opponentSide);
        
        // Get our possible moves.
        LinkedList moveList = getMoveList(board, side);

        // Return the move that results in the highest score.
        if (moveList.size() != 0) {
	    OthelloBoard newBoard;
	    ListIterator i = moveList.listIterator();
	    int highestscore = -1000000, currentscore;
	    Move bestmove = null, move;
	    while (i.hasNext()) {
		move = (Move) i.next();
		newBoard = board.copy();
		newBoard.move(move, side);
		currentscore = evaluateBoard(newBoard, side);
		currentscore -= RECURSION_WEIGHT * recurse(RecLevel(), newBoard, side.opposite());
		if (currentscore > highestscore) {
		    bestmove = move;
		    highestscore = currentscore;
		}
	    }
	    System.out.println("done iters, picked: " + bestmove);
            board.move(bestmove, side);
            return bestmove;
        }
        else {
            // PASS; no moves available.
            System.out.println("PASS");
            return null;
        }
    }

    
    /* Recursion to give the highest score for the move (optimistic..)
     * level is the current recursion level (starts at some level, goes to 0)
     * Will do all the possible moves and then act as the other player.
     * When called with a possible move already made, makes a possible move for
     * the opposite of <code>side</code>, and returns a score being positive
     * good for <code>side</code>
     */
    private int recurse(int level, OthelloBoard board, OthelloSide side) {
	if (level == 0)
	    return evaluateBoard(board, side);
	else {
	    // Get our possible moves.
	    LinkedList moveList = getMoveList(board, side);

	    if (moveList.size() != 0) {
		OthelloBoard newBoard;
		ListIterator i = moveList.listIterator(0);
		int highestscore = -1000000;
		Move bestmove = null, move;
		while (i.hasNext()) {
		    move = (Move) i.next();
		    newBoard = board.copy();
		    newBoard.move(move, side);
		    // positive good for side
		    int currentscore = evaluateBoard(newBoard, side);
		    currentscore -= RECURSION_WEIGHT * recurse(level-1,newBoard,side.opposite());
		    if (currentscore > highestscore) {
			bestmove = move;
			highestscore = currentscore;
		    }
		}
		return highestscore;
	    }
	    else {
		// Have to pass. not a good sign.  Negative score.
		return -35;
	    }
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
