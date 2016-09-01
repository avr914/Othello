import java.util.LinkedList;




/**
 * This class implements my own Othello AI.
 * The player classes I wrote are:
 * 		MyPlayer - Original attempt
 * 		MyPlayerAlt - Tuned and slightly modified version of MyPlayer that performs best
 * 		MyPlayerOpt - Alternative version of MyPlayer using a different data structure for a gameboard
 * @author Arvind Vijayakumar
 */

public class MyPlayerOpt implements OthelloPlayer {

	private int side;
	private int opponentSide;
	private static final int BLACK = 0;
	private static final int WHITE = 1;
	
	//AI's copy/account of board
	private OptimizedOthelloBoard board = OptimizedOthelloBoard.getDefaultBoard();
	
	
	public void init(OthelloSide side) {
		this.side = (side == OthelloSide.BLACK?
		        OptimizedOthelloBoard.BLACK : OptimizedOthelloBoard.WHITE);
        opponentSide = (1 - this.side);
	}

	int turn = 0;
	private final int MAX_DEPTH = 6;
	public Move doMove(Move opponentsMove, long millisLeft) {
		//Reflects opponents move on AI copy of board
		//System.out.println("Start of doMove.");
		System.out.println(" \n\n\n\n||||>> Turn: " + turn + " <<||||\n\n\n\n");
		Move move = new Move();
		if(opponentsMove != null) {
			board.makeMove(opponentSide, opponentsMove.getX(), opponentsMove.getY());
		}
		if(getMoveList(board, side).size() == 0) {
			return null;
		}
		//Move chosenMove = null;
		//if(chosenMove != null) { System.out.println("chosenMove" + chosenMove); }
		//else{ System.out.println("chosenMove == null"); }
		Pair<Double, Move> store = minimax(board, side, 0, MAX_DEPTH, move, -Double.MAX_VALUE, Double.MAX_VALUE);
		Move chosenMove = store.move;
		//int maxDepth = 8;
		//alt(board, side,  chosenMove, -Float.MAX_VALUE, Float.MAX_VALUE, 0, maxDepth);
		turn++;
		if(board.isLegalMove(side, chosenMove.getX(), chosenMove.getY())) {
			System.out.println("ChosenMove: " + chosenMove.toString());
			//System.out.println("FINAL:theScore: " + store.score);
			System.out.println("Other Current Score: " + evaluator(board, 1 - side));
			System.out.println("Computer Current Score: " + evaluator(board, side));
			board.makeMove(side, chosenMove.getX(), chosenMove.getY());
			return chosenMove;
		} else {
			return null;
		}
	}
	/*OthelloMove bestFound = new OthelloMove();
	 int maxDepth = 8;
	 minimax(board, bestFound, -Float.MAX_VALUE, Float.MAX_VALUE, maxDepth);
	//Wait for Thread to finish
	 board.makeMove(bestFound);*/
	
	public class Pair<Double, Move> { 
		  public double score; 
		  public Move move; 
		  public Pair(double x, Move y) { 
		    this.score = x; 
		    this.move = y; 
		  } 
		}
	
	/**
	 * Main minimax recursive method. 
	 * Note: Current alpha-beta optimization implementation is faulty, needs to be corrected.
	 * Note: Heuristic must be improved to avoid choosing corner-adjacent spots.
	 */
	private Pair<Double, Move> minimax(OptimizedOthelloBoard board, int side, int depth, int max_depth, 
								Move m, double alpha, double beta) {
		double bestScore;
		int turncount = turn;
		Move bestMove;
		//System.out.println("Start of minimax. Depth: " + depth + " Side: " + side);
	  /*  int state = board.getState();
		if (state == OthelloBoard.DRAW)
	        return new Pair<Double, Move>(0, m);
	    if ((state == OthelloBoard.BLACK_WINS) && (side == OthelloSide.BLACK))                    
	        return new Pair<Double, Move>(Double.POSITIVE_INFINITY, m);        
	    if ((state == OthelloBoard.WHITE_WINS) && (side == OthelloSide.WHITE))
	        return new Pair<Double, Move>(Double.POSITIVE_INFINITY, m);
	    if ((state == OthelloBoard.BLACK_WINS) && (side == OthelloSide.WHITE))
	        return new Pair<Double, Move>(Double.NEGATIVE_INFINITY, m);
	    if ((state == OthelloBoard.WHITE_WINS) && (side == OthelloSide.BLACK))
	        return new Pair<Double, Move>(Double.NEGATIVE_INFINITY, m);*/
		if(board.isGameOver()) {
			double endgame = (double) board.rawScore(side);
			if(side == this.side) {
				return new Pair<Double, Move>(endgame, m);
			} else {
				return new Pair<Double, Move>(-endgame, m);
			}
		}
		if(depth == max_depth) {
			double mdScore = evaluator(board, side);
			return new Pair<Double, Move>(mdScore, m);			
		} else {
			LinkedList<Move> moveList = getMoveList(board, side);
			if(depth == 0) {
	 			LinkedList<Move> corners = new LinkedList<Move>();
				for(Move mv : moveList) {
					if(board.isCorner(mv)) {
						corners.add(mv);
					}
				}
				if(corners.size() != 0) {
					Move bcorner = null;
					double best = -Double.MAX_VALUE;
					for(Move ml : corners) {
						double temp = evalMove(board, side, ml);
						if(temp > best) {
							best = temp;
							bcorner = ml;
						}
					}
					return new Pair<Double, Move>(best, bcorner);
				}
			}
			//System.out.println(moveList.toString());
			bestScore = -Double.MAX_VALUE;
			bestMove = new Move(1,1);
			if(moveList.size() == 0) {
				double mdScore = evaluator(board, side);
				return new Pair<Double, Move>(mdScore, m);
			} else {
				for(int i = 0; i < moveList.size(); i++) {
					OptimizedOthelloBoard tempBoard = board.copy();
					Move move = moveList.get(i);
					tempBoard.makeMove(side, move.getX(), move.getY());
					alpha = -(minimax(tempBoard, 1- side, depth + 1, max_depth, move, -beta, -alpha)).score;
					//System.out.println("Side: " + side);
					//System.out.println("alpha (before IF): " + alpha);
					//System.out.println("bestScore (before IF): " + bestScore);
					if(beta <= alpha) {
						return new Pair<Double, Move>(alpha, move);
					}
					if(alpha > bestScore ) {
						bestScore = alpha;
						bestMove = move;
						//bestMove.copy(move);
						//System.out.println("theScore(IF): " + alpha);
						//System.out.println("bestScore(IF): " + bestScore);
					}
				}
				return new Pair<Double, Move>(bestScore, bestMove);
			}
		}
	}
	
	private double evaluator(OptimizedOthelloBoard board, int side) {
		double pieceEval;
		double stability;
		double mobility;
		double pieceCount;
		double corner;
		double nextCorner;
		double score = 0;
		//double n = 1;
		//pieceEval
		double p = pieceCount(board, side);
		pieceCount = 10*p;
		//stability
		double s = stability(board, side);
		stability = 74.396*s;
		//mobility
		double m = mobility(board, side);
		//mobility = 30*m*Math.pow(0.86, turnCount);
		mobility = 78.922*m;
		//pieceCount
		double pE = pieceEval(board, side);
		//pieceEval = 60*pE*Math.pow(0.96, turnCount);
		pieceEval = 10*pE;
		//corner
		double c = cornerOccupancy(board, side); 
		corner = 801.724*c;
		//nextCorner
		double nC = nextToCorner(board, side);
		nextCorner = 382.026*nC;
		//score
		score = pieceCount + stability + mobility + pieceEval + corner + nextCorner;
		return score;
		
	}
	
	private double cornerOccupancy(OptimizedOthelloBoard board, int side) {
		int bTiles = 0;
		int wTiles = 0;
		double score = 0;
		if(board.get(side, 0, 0)) {bTiles++;}
		if(board.get(side, 0, 7)) {bTiles++;}
		if(board.get(side, 7, 0)) {bTiles++;}
		if(board.get(side, 7, 7)) {bTiles++;}
		if(board.get(1 - side, 0, 0)) {wTiles++;}
		if(board.get(1- side, 0, 7)) {wTiles++;}
		if(board.get(1- side, 7, 0)) {wTiles++;}
		if(board.get(1 - side, 7, 7)) {wTiles++;}
		score = 25*(bTiles - wTiles);
		return score;
	}
	
	private double nextToCorner(OptimizedOthelloBoard board, int side) {
		int bTiles = 0;
		int wTiles = 0;
		double score = 0;
		//Top-Left Corner Area
		if(board.get(side, 0, 1)) {bTiles++;}
		else if(board.get(1 - side, 0, 1)) {wTiles++;}
		if(board.get(side, 1, 0)) {bTiles++;}
		else if(board.get(1 - side, 1, 0)) {wTiles++;}
		if(board.get(side, 1, 1)) {bTiles++;}
		else if(board.get(1 - side, 1, 1)) {wTiles++;}
		//Top-Right Corner Area
		if(board.get(side, 6, 0)) {bTiles++;}
		else if(board.get(1 - side, 6, 0)) {wTiles++;}
		if(board.get(side, 6, 1)) {bTiles++;}
		else if(board.get(1 - side, 6, 1)) {wTiles++;}
		if(board.get(side, 7, 1)) {bTiles++;}
		else if(board.get(1 - side, 7, 1)) {wTiles++;}
		//Bottom-Left Corner Area
		if(board.get(side, 0, 6)) {bTiles++;}
		else if(board.get(1 - side, 0, 6)) {wTiles++;}
		if(board.get(side, 1, 6)) {bTiles++;}
		else if(board.get(1 - side, 1, 6)) {wTiles++;}
		if(board.get(side, 1, 7)) {bTiles++;}
		else if(board.get(1 - side, 1, 7)) {wTiles++;}
		//Bottom-Right Corner Area
		if(board.get(side, 7, 6)) {bTiles++;}
		else if(board.get(1 - side, 7, 6)) {wTiles++;}
		if(board.get(side, 6, 6)) {bTiles++;}
		else if(board.get(1 - side, 6, 6)) {wTiles++;}
		if(board.get(side, 6, 7)) {bTiles++;}
		else if(board.get(1 - side, 6, 7)) {wTiles++;}
		score = -12.5*(bTiles - wTiles);
		return score;
	}
	
	private double pieceEval(OptimizedOthelloBoard board, int side) {
		int score = 0;
		int[][] values = { {20, -3, 11, 8, 8, 11, -3, 20},
				  			{-3, -7, -4, 1, 1, -4, -7, -3},
				  			{11, -4, 2, 2, 2, 2, -4, 11},
				  			{8, 1, 2, -3, -3, 2, 1, 8},
				  			{8, 1, 2, -3, -3, 2, 1, 8},
				  			{11, -4, 2, 2, 2, 2, -4, 11},
				  			{-3, -7, -4, 1, 1, -4, -7, -3},
				  			{20, -3, 11, 8, 8, 11, -3, 20}};
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if(board.get(side, x, y)) {
					score += values[y][x];
				} else if(board.get(1 - side, x, y)) {
					score -=values[y][x];
				}
			}
		}
		return score;
	}
	
	private double stability(OptimizedOthelloBoard board, int side) {
		boolean frontierBlack = false;
		boolean frontierWhite = false;
		int bPieces = 0;
		int wPieces = 0;
		int fScore = 0;
		LinkedList<Move> occupiedBlackList = getOccupiedPlaces(board, side);
		LinkedList<Move> occupiedWhiteList = getOccupiedPlaces(board, 1 - side);
		int[] xC = {1, 1, 0, -1, -1, -1, 1, 0};
		int[] yC = {0, 1, 1, 0, -1, 1, -1, -1};
		for(int i = 0; i < occupiedBlackList.size(); i++) {
			for(int k = 0; k < 8; k++) {
				int x1 = occupiedBlackList.get(i).getX();
				int y1 = occupiedBlackList.get(i).getY();
				int x = x1 + xC[k];
				int y = y1 + yC[k];
				if( x >= 0 && y >= 0 && !(board.isCorner(x1, y1))) {
					if(board.get(side, x1, y1) && !(board.occupied(x, y))) {
						frontierBlack = true;
					}
				}
			}
			if(frontierBlack) {bPieces++;}
		}
		for(int i = 0; i < occupiedWhiteList.size(); i++) {
			for(int k = 0; k < 8; k++) {
				int x1 = occupiedWhiteList.get(i).getX();
				int y1 = occupiedWhiteList.get(i).getY();
				int x = x1 + xC[k];
				int y = y1 + yC[k];
				if( x >= 0 && y >= 0 &&!(board.isCorner(x1, y1))) {
					if(board.get(1 - side, x1, y1) && !(board.occupied(x, y))) {
						frontierWhite = true;
					}
				}
			}
			if(frontierWhite) {wPieces++;}
		}
		if(bPieces > wPieces) {
			fScore = -100*bPieces/(bPieces + wPieces);
		} else if(bPieces < wPieces) {
			fScore = 100*wPieces/(bPieces + wPieces);
		}
		return fScore;
	}
	
	private double mobility(OptimizedOthelloBoard board, int side) {
		int m = 0;
		int my = getMoveList(board, side).size();
		int opp = getMoveList(board, 1 - side).size();
		if(my > opp) {
			m = 100*my/(my + opp);
		} else if(my < opp) {
			m = -100*opp/(my + opp);
		}
		return m;
	}
	
	private double pieceCount(OptimizedOthelloBoard board, int side) {
		int p = 0;
		int my = board.rawScore(side);
		int opp = board.rawScore(1 - side);
		if(my > opp) {
			p = 100*my/(my + opp);
		} else if(my < opp) {
			p = -100*opp/(my + opp);
		}
		return p;
	}
	
    // Returns a list of possible moves.
    public LinkedList getMoveList(OptimizedOthelloBoard board, int side) {
        LinkedList moveList = new LinkedList();
        Move move;
        for (int i = 0; i <= 7; i++)
            for (int j = 0; j <= 7; j++) {
                move = new Move(i,j);
                if (board.isLegalMove(side, i, j)) moveList.add(move);
            }
        return moveList;
    }
    
    private LinkedList<Move> getOccupiedPlaces(OptimizedOthelloBoard board, int side) {
    	LinkedList<Move> occupiedPlaces = new LinkedList<Move>();
    	Move move;
    	for(int x = 0; x < 8; x++) {
    		for(int y = 0; y < 8; y++) {
    			if(board.get(side, x, y)) {
    				move = new Move(x,y);
    				occupiedPlaces.add(move);
    			}
    		}
    	}
    	return occupiedPlaces;
    }
    
    public double evalMove(OptimizedOthelloBoard board, int side, Move m) {
 	   OptimizedOthelloBoard temp = board.copy();
 	   temp.makeMove(side, m.getX(), m.getY());
 	   double score = evaluator(temp, side);
 	   return score;
    }
}