/** This class implements a straightforward Othello AI. It calculates
 *  a list of possible moves, then goes through a series of recursions
 *  to find which is the best after a certain number of moves.
 *  Mainly for testing purposes.
 *  It implements OthelloPlayer.
 */

import java.util.*;

public class OogeePlayer implements OthelloPlayer {
    
    final static int RECURSION_LEVEL = 4;
    // The weight to apply to the score recurse computes.
    final static float RECURSION_WEIGHT = 5.85f;
    
    private int side;
    private int opponentSide;
    
    // A counter of moves, starting at 0 and going up to 30.
    private int moves = 0;
    
    // AI's copy of the board.
    private OptimizedOthelloBoard board = OptimizedOthelloBoard.getDefaultBoard();
    
    /** Initialize the AI. Doesn't do much other than set the side. */
    public void init(OthelloSide side) {
        this.side = (side == OthelloSide.BLACK?
        OptimizedOthelloBoard.BLACK : OptimizedOthelloBoard.WHITE);
        
        opponentSide = 1 - this.side;
    }
    
    /** Returns the next move.
     * Takes in the opponents' move.
     */
    public Move doMove(Move opponentsMove, long millisLeft) {
        
        // Increment move counter by 1 (for a complete move -- two half-moves)
        moves++;
        
        // Reflect oppenent's move on our copy of board
        if(opponentsMove != null)
            board.makeMove(opponentSide, opponentsMove.getX(),opponentsMove.getY());
        
        // Get our possible moves.
        LinkedList moveList = getMoveList(board, side);
        
        // Return the move that results in the highest score.
        if (moveList.size() != 0) {
            OptimizedOthelloBoard newBoard;
            Collections.shuffle(moveList);
            ListIterator i = moveList.listIterator();
            int highestscore = Integer.MIN_VALUE, currentscore;
            Move bestmove = null, move;
            while (i.hasNext()) {
                move = (Move) i.next();
                newBoard = board.copy();
                newBoard.makeMove(side, move.getX(), move.getY());
                currentscore = evaluateBoard(newBoard, side);
                currentscore -= RECURSION_WEIGHT * recurse(RECURSION_LEVEL,
                newBoard, 1 - side);
                if (currentscore > highestscore) {
                    bestmove = move;
                    highestscore = currentscore;
                }
            }
            System.out.println("done iters, picked: " + bestmove);
            board.makeMove(side, bestmove.getX(), bestmove.getY());
            System.out.println(board);
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
    private int recurse(int level, OptimizedOthelloBoard board, int side) {
        if (level == 0)
            return evaluateBoard(board, side);
        else {
            // Get our possible moves.
            LinkedList moveList = getMoveList(board, side);
            
            if (moveList.size() != 0) {
                OptimizedOthelloBoard newBoard;
                
                Collections.shuffle(moveList);
                
                ListIterator i = moveList.listIterator(0);
                int highestscore = Integer.MIN_VALUE;
                Move bestmove = null, move;
                
                
                while (i.hasNext()) {
                    move = (Move) i.next();
                    newBoard = board.copy();
                    newBoard.makeMove(side, move.getX(), move.getY());
                    // positive good for side
                    int currentscore = evaluateBoard(newBoard, side);
                    
                    currentscore -= RECURSION_WEIGHT * recurse(level-1, newBoard,
                    1 - side);
                    
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
    private int evaluateBoard(OptimizedOthelloBoard board, int side) {
        int score = 0;
        int ownpieces = 0;
        int opppieces = 0;
        
        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++) {
                if (board.getState(x, y) == side) {
                    score += evaluatePiece(board, side, x, y);
                    ownpieces++;
                } else if (board.getState(x, y) == opponentSide) {
                    score -= evaluatePiece(board, opponentSide, x, y);
                    opppieces++;
                }
            }
        if (ownpieces == 0) {
            score = Integer.MIN_VALUE * 100;
        }
        
        if (opppieces == 0) {
            score = Integer.MAX_VALUE * 100;
        }
        
        if (moves > 28) {
            score = ownpieces - opppieces;
        }
        
        return score;
    }
    
    private int evaluatePiece(OptimizedOthelloBoard board, int side, int x, int y) {
        
        int score;
        
        // -1 or 1 is the standard score.
        if (moves < 25) {
            score = -1;
        } else {
            score = 1;
        }
        
        // Add 30 for a corner piece.
        if ((x == 0 || x == 7) && (y == 0 || y ==7))
            score += 100;
        
        // Add 5 for a side piece.
        if (x == 0 || x == 7 || y == 0 || y == 7)
            score += 10;
        
        // Subtract 1 for the adjacent-to-sides.
        if (x == 1 || x == 6 || y == 1 || y == 6) {
            score -= 2;
            
            // Subtract 5 if you are adjacent to a corner but not in that corner.
            if ((x == 1 && y == 1) && !(board.getState(0, 0) == side))
                score -= 25;
            if ((x == 1 && y == 6) && !(board.getState(0, 7) == side))
                score -= 25;
            if ((x == 6 && y == 1) && !(board.getState(7, 0) == side))
                score -= 25;
            if ((x == 6 && y == 6) && !(board.getState(7, 7) == side))
                score -= 25;
        }
        
        return score;
    }
    
    // Returns a list of possible moves.
    private LinkedList getMoveList(OptimizedOthelloBoard board, int side) {
        LinkedList moveList = new LinkedList();
        Move move;
        for (int i = 0; i <= 7; i++)
            for (int j = 0; j <= 7; j++) {
                move = new Move(i,j);
                if (board.isLegalMove(side, i, j)) moveList.add(move);
            }
        return moveList;
    }
    
}
