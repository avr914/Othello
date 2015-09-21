#include "player.h"

/*
 * Constructor for the player; initialize everything here. The side your AI is
 * on (BLACK or WHITE) is passed in as "side". The constructor must finish 
 * within 30 seconds.
 */
Player::Player(Side side) {
    testingMinimax = false;

    /* 
     * TODO: Do any initialization you need to do here (setting up the board,
     * precalculating things, etc.) However, remember that you will only have
     * 30 seconds.
     */
    
    /* TODO: Basic reference implementation, remove! */    
    this->side = side;
    oppSide = (side == BLACK) ? WHITE : BLACK;    
}

/*
 * Destructor for the player.
 */
Player::~Player() {
}


/*
 * Compute the next move given the opponent's last move. Each AI is
 * expected to keep track of the board on its own. If this is the first move,
 * or if the opponent passed on the last move, then opponentsMove will be NULL.
 *
 * If there are no valid moves for your side, doMove must return NULL.
 *
 * Important: doMove must take no longer than the timeout passed in 
 * msLeft, or your AI will lose! The move returned must also be legal.
 */
Move *Player::doMove(Move *opponentsMove, int msLeft) {
    /* 
     * TODO: Implement how moves your AI should play here. You should first
     * process the opponent's opponents move before calculating your own move
     */ 

    /* TODO: Basic reference implementation, remove! */
    board.doMove(opponentsMove, oppSide);

    bestMoveX = -1;
    bestMoveY = -1;

    miniMax(&board, side, 0);

    if (bestMoveX == -1 && bestMoveY == -1) {
        return NULL;
    } else {
        Move *bestMove = new Move(bestMoveX, bestMoveY);
        board.doMove(bestMove, side);
        return bestMove;
    }
}

int Player::miniMax(Board *board, Side currSide, int depth) {
    if (depth == 2) {
        return board->count(side) - board->count(oppSide);
    }
    int bestScore = -100;    
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Move move(i, j);     
            if (board->checkMove(&move, currSide)) {
                Board *newBoard = board->copy();
                newBoard->doMove(&move, currSide);
                
                int tempScore = miniMax(newBoard, (currSide == BLACK) ? WHITE : BLACK, depth + 1);
                if (currSide == oppSide) tempScore *= -1;
                if (tempScore > bestScore) {
                    bestScore = tempScore;
                    if (depth == 0) {
                        bestMoveX = move.x;
                        bestMoveY = move.y;
                    }
                }
                delete newBoard;
            }
        }
    }
    if (currSide == oppSide) bestScore *= -1;    
    return bestScore;
}

void Player::setBoard(Board *board) {
    this->board = *board;
}
