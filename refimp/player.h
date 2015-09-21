#ifndef __PLAYER_H__
#define __PLAYER_H__

#include <iostream>
#include "common.h"
#include "board.h"
using namespace std;

static int boardScores[8][8] = {
    {50, -1, 5, 2, 2, 5, -1, 50},
    {-1, -10, 1, 1, 1, 1, -10, -1},
    {5, 1, 1, 1, 1, 1, 1, 5},
    {2, 1, 1, 0, 0, 1, 1, 2},
    {2, 1, 1, 0, 0, 1, 1, 2},
    {5, 1, 1, 1, 1, 1, 1, 5},
    {-1, -10, 1, 1, 1, 1, -10, -1},
    {50, -1, 5, 2, 2, 5, -1, 50}
};

class Player {
private:
    // TODO: Part of refimp; remove!
    Side side;
    Side oppSide;
    Board board;
    int bestMoveX;
    int bestMoveY;


public:
    Player(Side side);
    ~Player();
    
    Move *doMove(Move *opponentsMove, int msLeft);
    int miniMax(Board *board, Side currSide, int depth);
    int evaluateMove(Move *move, Board *board);
    void setBoard(Board *board);

    // Flag to tell if the player is running within the test_minimax context
    bool testingMinimax;        
};

#endif
