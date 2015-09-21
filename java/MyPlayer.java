import java.util.*;



/**
 * This class implements my own Othello AI.
 * @author Arvind Vijayakumar
 */

public class MyPlayer implements OthelloPlayer {

	private OthelloSide side;
	private OthelloSide opponentSide;
	
	//AI's copy/account of board
	private OthelloBoard board = new OthelloBoard();
	
	
	public void init(OthelloSide side) {
        this.side = side;
        if (side == OthelloSide.BLACK)
            opponentSide = OthelloSide.WHITE;
        else
            opponentSide = OthelloSide.BLACK;
	}

	/*public Move doMove2(Move opponentsMove, long millisLeft) {
		
		Move move;
		//Reflects opponents move on AI copy of board
		if(opponentsMove != null) {
			board.move(opponentsMove, opponentSide);
		}
		//Get list of possible moves
		LinkedList<Move> moveList = getMoveList(board,side);
		OthelloBoard tempBoard = board.copy();
		int maxScore = 0;
		int moveIndex = 0;
		int tempScore;
		if(moveList.size() != 0) {
			for(int i = 0; i < moveList.size(); i++) {
				move = moveList.get(i);
				tempBoard.move(move, side);
				tempScore = tempBoard.score(side);
				if(tempScore > maxScore) {
					maxScore = tempScore;
					moveIndex = i;
					System.out.println(maxScore);
				}
				tempBoard = board.copy();
			}
			move = moveList.get(moveIndex);
			board.move(move, side);
		} else {
			System.out.println("PASS");
			return null;
		}

		return move;
	}**/
	int turn = 0;
	public Move doMove(Move opponentsMove, long millisLeft) {
		//Reflects opponents move on AI copy of board
		//System.out.println("Start of doMove.");
		System.out.println(" \n\n\n\n||||>> Turn: " + turn + " <<||||\n\n\n\n");
		Move move = new Move();
		if(opponentsMove != null) {
			board.move(opponentsMove, opponentSide);
		}
		if(getMoveList(board, side).size() == 0) {
			return null;
		}
		//Move chosenMove = null;
		//if(chosenMove != null) { System.out.println("chosenMove" + chosenMove); }
		//else{ System.out.println("chosenMove == null"); }
		Pair<Double, Move> store = minimax(board, side, 0, 6, move, -Double.MAX_VALUE, Double.MAX_VALUE);
		Move chosenMove = store.move;
		//int maxDepth = 8;
		//alt(board, side,  chosenMove, -Float.MAX_VALUE, Float.MAX_VALUE, 0, maxDepth);
		turn++;
		if(board.checkMove(chosenMove, side)) {
			System.out.println("ChosenMove: " + chosenMove.toString());
			//System.out.println("FINAL:theScore: " + store.score);
			System.out.println("Other Current Score: " + evaluator(board, side.opposite()));
			System.out.println("Computer Current Score: " + evaluator(board, side));
			board.move(chosenMove, side);
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
	private Pair<Double, Move> minimax(OthelloBoard board, OthelloSide side, int depth, int max_depth, 
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
		if(board.isDone()) {
			double endgame = board.countBlack() - board.countWhite();
			return new Pair<Double, Move>(endgame, m);
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
					OthelloBoard tempBoard = board.copy();
					Move move = moveList.get(i);
					tempBoard.move(move, side);
					alpha = -(minimax(tempBoard, side.opposite(), depth + 1, max_depth, move, -beta, -alpha)).score;
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
	
	private double evaluator(OthelloBoard board, OthelloSide side) {
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
	
	private double cornerOccupancy(OthelloBoard board, OthelloSide side) {
		int bTiles = 0;
		int wTiles = 0;
		double score = 0;
		if(board.get(OthelloSide.BLACK, 0, 0)) {bTiles++;}
		if(board.get(OthelloSide.BLACK, 0, 7)) {bTiles++;}
		if(board.get(OthelloSide.BLACK, 7, 0)) {bTiles++;}
		if(board.get(OthelloSide.BLACK, 7, 7)) {bTiles++;}
		if(board.get(OthelloSide.WHITE, 0, 0)) {wTiles++;}
		if(board.get(OthelloSide.WHITE, 0, 7)) {wTiles++;}
		if(board.get(OthelloSide.WHITE, 7, 0)) {wTiles++;}
		if(board.get(OthelloSide.WHITE, 7, 7)) {wTiles++;}
		score = 25*(bTiles - wTiles);
		return score;
	}
	
	private double nextToCorner(OthelloBoard board, OthelloSide side) {
		int bTiles = 0;
		int wTiles = 0;
		double score = 0;
		//Top-Left Corner Area
		if(board.get(OthelloSide.BLACK, 0, 1)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 0, 1)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 1, 0)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 1, 0)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 1, 1)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 1, 1)) {wTiles++;}
		//Top-Right Corner Area
		if(board.get(OthelloSide.BLACK, 6, 0)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 6, 0)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 6, 1)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 6, 1)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 7, 1)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 7, 1)) {wTiles++;}
		//Bottom-Left Corner Area
		if(board.get(OthelloSide.BLACK, 0, 6)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 0, 6)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 1, 6)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 1, 6)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 1, 7)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 1, 7)) {wTiles++;}
		//Bottom-Right Corner Area
		if(board.get(OthelloSide.BLACK, 7, 6)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 7, 6)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 6, 6)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 6, 6)) {wTiles++;}
		if(board.get(OthelloSide.BLACK, 6, 7)) {bTiles++;}
		else if(board.get(OthelloSide.WHITE, 6, 7)) {wTiles++;}
		score = -12.5*(bTiles - wTiles);
		return score;
	}
	
	private double pieceEval(OthelloBoard board, OthelloSide side) {
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
				if(board.get(OthelloSide.BLACK, x, y)) {
					score += values[y][x];
				} else if(board.get(OthelloSide.WHITE, x, y)) {
					score -=values[y][x];
				}
			}
		}
		return score;
	}
	
	private double stability(OthelloBoard board, OthelloSide side) {
		boolean frontierBlack = false;
		boolean frontierWhite = false;
		int bPieces = 0;
		int wPieces = 0;
		int fScore = 0;
		LinkedList<Move> occupiedBlackList = getOccupiedPlaces(board, OthelloSide.BLACK);
		LinkedList<Move> occupiedWhiteList = getOccupiedPlaces(board, OthelloSide.WHITE);
		int[] xC = {1, 1, 0, -1, -1, -1, 1, 0};
		int[] yC = {0, 1, 1, 0, -1, 1, -1, -1};
		for(int i = 0; i < occupiedBlackList.size(); i++) {
			for(int k = 0; k < 8; k++) {
				int x1 = occupiedBlackList.get(i).getX();
				int y1 = occupiedBlackList.get(i).getY();
				int x = x1 + xC[k];
				int y = y1 + yC[k];
				if( x >= 0 && y >= 0 && !(board.isCorner(x1, y1))) {
					if(board.get(OthelloSide.BLACK, x1, y1) && !(board.occupied(x, y))) {
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
					if(board.get(OthelloSide.WHITE, x1, y1) && !(board.occupied(x, y))) {
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
	
	private double mobility(OthelloBoard board, OthelloSide side) {
		int m = 0;
		int black = getMoveList(board, OthelloSide.BLACK).size();
		int white = getMoveList(board, OthelloSide.WHITE).size();
		if(black > white) {
			m = 100*black/(black + white);
		} else if(black < white) {
			m = -100*white/(black + white);
		}
		return m;
	}
	
	private double pieceCount(OthelloBoard board, OthelloSide side) {
		int p = 0;
		int black = board.countBlack();
		int white = board.countWhite();
		if(black > white) {
			p = 100*black/(black + white);
		} else if(black < white) {
			p = -100*white/(black + white);
		}
		return p;
	}
	
	//Heuristic Evaluation weights:
	//	double score = (10 * p) + (801.724 * c) + (382.026 * l) + (78.922 * m) + (74.396 * f) + (10 * d);

	/*private double heuristic(OthelloBoard board, OthelloSide side) {
		boolean frontier = false;
		int myPieces = 0;
		int oppPieces = 0;
		int myFrontPieces = 0;
		int oppFrontPieces = 0;
		int x1[] = {-1, -1, 0, 1, 1, 1, 0, -1};
		int y1[] = {0, 1, 1, 1, 0, -1, -1, -1};
		int i,j;
		int[][] values = { {20, -3, 11, 8, 8, 11, -3, 20},
				  {-3, -7, -4, 1, 1, -4, -7, -3},
				  {11, -4, 2, 2, 2, 2, -4, 11},
				  {8, 1, 2, -3, -3, 2, 1, 8},
				  {8, 1, 2, -3, -3, 2, 1, 8},
				  {11, -4, 2, 2, 2, 2, -4, 11},
				  {-3, -7, -4, 1, 1, -4, -7, -3},
				  {20, -3, 11, 8, 8, 11, -3, 20}};
		//Main output values
		double d = 0;
		double p = 0;
		double f = 0;
		double c = 0;
		double l = 0;
		double m = 0;
		double score = 0;
		//d - Value Evaluator
		for(int x = 0; x <=7; x++) {
			for(int y = 0; y <= 7; y++) {
			   if(board.get(side, x, y)) {
				   d += values[y][x];
				   //System.out.println("if-a(temp) : " + a);
				   myPieces++;
			   } else if(board.get(side.opposite(), x, y)) {
				   d -= values[y][x];
				   oppPieces++;
				   //System.out.println("elseif-a(temp) : " + a);
			   }
			   for(int k = 0; k <= 7; k++) {
				   if(!frontier) {
					   i = x + x1[k];
					   j = y + y1[k];
					   if( i >= 0 && j >= 0) {
						   if(!(board.occupied(i,j)) && board.get(side, x, y)) {
							   myFrontPieces++;
							   frontier = true;
						   }
						   if(!(board.occupied(i,j)) && board.get(side.opposite(), x ,y)) {
							   oppFrontPieces++;
							   frontier = true;
						   }
					   }
				   }
			   }
			}
		}
		//System.out.println("myPieces : " + myPieces);
		//System.out.println("oppPieces : " + oppPieces);
		//int totalPieces = board.countBlack() + board.countWhite();
		//System.out.println("totalPieces : " + totalPieces);
		//System.out.println("\nd : " + d);
		//p - Piece Difference Coefficient
		if(myPieces > oppPieces) {
			p = 100*myPieces/(double) (myPieces+oppPieces);
		} else if(oppPieces > myPieces) {
			p = -100*oppPieces/(double) (myPieces+oppPieces);
		} else {
			p = 0;
		}
		//System.out.println("p : " + p);
		//f - Frontier Discs
		if(myFrontPieces > oppFrontPieces) {
			f = -100*myFrontPieces/ (double) (myFrontPieces + oppFrontPieces);
		} else if(oppFrontPieces > myFrontPieces) {
			f = 100*oppFrontPieces/(double) (myFrontPieces + oppFrontPieces);
		}
		//System.out.println("f : " + f);
		//c - Corner Occupancy Coefficient
		int mTiles = 0;
		int oTiles = 0;
		if(board.get(side, 0, 0)) {mTiles++;}
		if(board.get(side, 0, 7)) {mTiles++;}
		if(board.get(side, 7, 0)) {mTiles++;}
		if(board.get(side, 7, 7)) {mTiles++;}
		if(board.get(side.opposite(), 0, 0)) {oTiles++;}
		if(board.get(side.opposite(), 0, 7)) {oTiles++;}
		if(board.get(side.opposite(), 7, 0)) {oTiles++;}
		if(board.get(side.opposite(), 7, 7)) {oTiles++;}
		c = 25*mTiles - 25*oTiles;
		//System.out.println("c : " + c);
		//e - NextToCorner Occupancy Coefficient
		mTiles = oTiles = 0;
		//Top-Left Corner Area
		if(board.get(side, 0, 1)) {mTiles++;}
		else if(board.get(side.opposite(), 0, 1)) {oTiles++;}
		if(board.get(side, 1, 0)) {mTiles++;}
		else if(board.get(side.opposite(), 1, 0)) {oTiles++;}
		if(board.get(side, 1, 1)) {mTiles++;}
		else if(board.get(side.opposite(), 1, 1)) {oTiles++;}
		//Top-Right Corner Area
		if(board.get(side, 6, 0)) {mTiles++;}
		else if(board.get(side.opposite(), 6, 0)) {oTiles++;}
		if(board.get(side, 6, 1)) {mTiles++;}
		else if(board.get(side.opposite(), 6, 1)) {oTiles++;}
		if(board.get(side, 7, 1)) {mTiles++;}
		else if(board.get(side.opposite(), 7, 1)) {oTiles++;}
		//Bottom-Left Corner Area
		if(board.get(side, 0, 6)) {mTiles++;}
		else if(board.get(side.opposite(), 0, 6)) {oTiles++;}
		if(board.get(side, 1, 6)) {mTiles++;}
		else if(board.get(side.opposite(), 1, 6)) {oTiles++;}
		if(board.get(side, 1, 7)) {mTiles++;}
		else if(board.get(side.opposite(), 1, 7)) {oTiles++;}
		//Bottom-Right Corner Area
		if(board.get(side, 7, 6)) {mTiles++;}
		else if(board.get(side.opposite(), 7, 6)) {oTiles++;}
		if(board.get(side, 6, 6)) {mTiles++;}
		else if(board.get(side.opposite(), 6, 6)) {oTiles++;}
		if(board.get(side, 6, 7)) {mTiles++;}
		else if(board.get(side.opposite(), 6, 7)) {oTiles++;}
		
		l = -12.5*mTiles + 12.5*oTiles;
		//System.out.println("l : " + l);
		
		//m - Mobility
		mTiles = getMoveList(board, side).size();
		oTiles = getMoveList(board, side.opposite()).size();
		if(mTiles > oTiles) {
			m = 100*mTiles/ (double) (mTiles + oTiles);
		} else if(oTiles > mTiles) {
			m = -100*oTiles/ (double) (mTiles + oTiles);
		} else {
			m = 0;
		}
		//System.out.println("m : " + m + "\n");
		//score - final constant
		score = (10 * p) + (801.724 * c) + (382.026 * l) + (78.922 * m) + (74.396 * f) + (10 * d);
		//System.out.println("\nscore : " + score + "\n");
		return score;
	}*/
	
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
    
    private LinkedList<Move> getOccupiedPlaces(OthelloBoard board, OthelloSide side) {
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
    
    public double evalMove(OthelloBoard board, OthelloSide side, Move m) {
 	   OthelloBoard temp = board.copy();
 	   temp.move(m, side);
 	   double score = evaluator(temp, side);
 	   return score;
    }
    //Incomplete/Alternate Methods:
    //1)alt
    //2)miniMax-integer implementation of pair minimax
    /*private float alt(OthelloBoard board, OthelloSide side, Move best, float alpha, float beta, int depth, int maxDepth)
	{
	    System.out.println("Start of alt. Depth: " + depth);
		float bestResult = -Float.MAX_VALUE;
	    Move garbage = null;
	    
	    int state = board.getState();
	    System.out.println("State: " + state + " Side: " + side);
	    //int currentPlayer = board.getCurrentPlayer();

	    if (state == OthelloBoard.DRAW)
	        return 0.0f;
	    if ((state == OthelloBoard.BLACK_WINS) && (side == OthelloSide.BLACK))                    
	        return Float.POSITIVE_INFINITY;        
	    if ((state == OthelloBoard.WHITE_WINS) && (side == OthelloSide.WHITE))
	        return Float.POSITIVE_INFINITY;
	    if ((state == OthelloBoard.BLACK_WINS) && (side == OthelloSide.WHITE))
	        return Float.NEGATIVE_INFINITY;
	    if ((state == OthelloBoard.WHITE_WINS) && (side == OthelloSide.BLACK))
	        return Float.NEGATIVE_INFINITY;
		if (depth == maxDepth) {
			int temp = board.rawScore(this.side);
			if(side == this.side) {
				System.out.println("rawScore: " + temp);
				return temp; 
			} else {
				System.out.println("rawScore: " + temp);
				return -temp;
			}
		}
			
	    
	    LinkedList<Move> moveList = getMoveList(board, side);

	    for (Move mv : moveList)
	    {            
	        OthelloBoard tempBoard = board.copy();
	    	tempBoard.move(mv, side);
	        alpha = - alt(tempBoard, side.opposite(), garbage, -beta, -alpha, depth + 1, maxDepth);
	        System.out.println("Alpha: " + alpha);
	        System.out.println("Beta: " + beta);
	        System.out.println("bestResult: " + bestResult);
	        if (beta <= alpha) {
	        	System.out.println("If beta <= alpha: True");
	            return alpha;
	    	}    
	        if (alpha > bestResult)
	        {                
	        	System.out.println("If alpha > bestResult: True");
	            //best.setFlipSquares(mv.getFlipSquares());
	            //best.setIdx(mv.getIdx());        
	            //best.setPlayer(mv.getPlayer());
	        	if(mv != null)
	        		System.out.println("mv == " + mv.toString());
	        	else
	        		System.out.println("mv == null");
	        	best.copy(mv);
	            bestResult = alpha;
	        }
	    }

	     return bestResult;
	}*/
	
	/*private int miniMax(OthelloBoard board, OthelloSide side, int depth, int max_depth, Move m) {
		int bestScore;
		Move bestMove;
		System.out.println("Start of minimax. Depth: " + depth + " Side: " + side);
		if(depth == max_depth) {
			if(side == this.side)
				return board.rawScore(this.side);
			else
				return -board.rawScore(this.side);
		} else {
			LinkedList<Move> moveList = getMoveList(board, side);
			System.out.println(moveList.toString());
			bestScore = 0;
			bestMove = new Move(1,1);
			if(moveList.size() == 0) {
				if(side == this.side)
					return board.rawScore(this.side);
				else
					return -board.rawScore(this.side);
			} else {
				for(int i = 0; i < moveList.size(); i++) {
					OthelloBoard tempBoard = board.copy();
					Move move = moveList.get(i);
					tempBoard.move(move, side);
					int theScore = miniMax(tempBoard, side.opposite(), depth + 1, max_depth, move);
					System.out.println("Side: " + side);
					System.out.println("theScore (before IF): " + theScore);
					System.out.println("bestScore (before IF): " + bestScore);
					if(theScore > bestScore ) {
						bestScore = theScore;
						bestMove = move;
						bestMove.copy(move);
						System.out.println("theScore(IF): " + theScore);
						System.out.println("bestScore(IF): " + bestScore);
					}
				}
				m.copy(bestMove);
				return bestScore;
			}
		}
	}*/

}
