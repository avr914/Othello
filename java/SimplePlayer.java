import java.util.*;

/** This class implements a simple (dumb) Othello AI. It calculates
 *  a list of possible moves, then picks one at random.
 *  This player is mainly for testing purposes.
 *  It implements {@link OthelloPlayer}.
 */

public class SimplePlayer implements OthelloPlayer {
    
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
        
        Move move;
        
        // Reflect oppenent's move on our copy of board
        if (opponentsMove != null) 
            board.move(opponentsMove, opponentSide);
        
        // Get our possible moves.
        LinkedList<Move> moveList = getMoveList(board, side);
        
        // Return a randomly picked move.
        if (moveList.size() != 0) {
            move = (Move) moveList.get(gen.nextInt(moveList.size()));
            board.move(move, side);
            return move;
        }
        else {
            // PASS; no moves available.
            System.out.println("PASS");
            return null;
        }
    }
    
    // Returns a list of possible moves.
    private LinkedList<Move> getMoveList(OthelloBoard board, OthelloSide side) {
        Move move;
        LinkedList<Move> moveList = new LinkedList<Move>();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                move = new Move(i,j);
                if (board.checkMove(move, side)) {
                    moveList.add(move);
                }
            }
        return moveList;
    }
}
