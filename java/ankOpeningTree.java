/*
 * ankOpeningTree.java
 *
 * Created on March 8, 2005, 2:51 PM
 */

import java.util.*;

/**
 *
 * @author Andrew N. Kwok
 */
public class ankOpeningTree {
   private Move currMove;
   private Move bestMove;
   private LinkedList<ankOpeningTree> children;
   
   private double score;
   
   public static final int C4 = 0; // First move at (2, 3), no change in book
   public static final int D3 = 1; // First move at (3, 2), inverse
   public static final int F5 = 2; // First move at (5, 4), 180 degree rotation
   public static final int E6 = 3; // First move at (4, 5), inverse & 180 degree rotation
   
   /** Creates a new instance of ankOpeningTree */
   public ankOpeningTree() {
      children = new LinkedList<ankOpeningTree>();
   }
   
   public void addOpening(LinkedList<Move> moves, double score, ankOpeningTree node) {
      node.currMove = moves.removeFirst();
      
      if (moves.size() == 0) {
         node.score = score;
      } else {
         Move m = moves.getFirst();
         boolean dupe = false;
         
         for (ankOpeningTree child : node.children) {
            if (child.getMove().equals(m)) {
               addOpening(moves, score, child);
               dupe = true;
               break;
            }
         }
         
         if (dupe == false) {
            ankOpeningTree child = new ankOpeningTree();
            node.children.add(child);
            addOpening(moves, score, child);
         }
      }
   }
   
   public ankOpeningTree getOpening(Move m) {
      for (ankOpeningTree child : children) {
         if (child.getMove().equals(m))
            return child;
      }
      
      // the given move is not in the opening book or there are no more children
      return null;
   }
   
   public double searchScores(int color, ankOpeningTree node) {
      if (node.children.size() == 0) {
         return node.score;
      } else {
         double bestScore;
         double tempScore;
         Move bestMove = new Move(0,0);
         
         if (color == ankBoard.BLACK) {
            bestScore = -100.0;
            for (ankOpeningTree child : node.children) {
               tempScore = searchScores(-color, child);
               if (tempScore > bestScore) {
                  bestScore = tempScore;
                  bestMove = child.currMove;
               }
            }
         } else {
            bestScore = 100.0;
            for (ankOpeningTree child : node.children) {
               tempScore = searchScores(-color, child);
               if (tempScore < bestScore) {
                  bestScore = tempScore;
                  bestMove = child.currMove;
               }
            }
         }
         
         node.score = bestScore;
         node.bestMove = bestMove;
         return bestScore;
      }
   }
   
   public Move getMove() {
      return currMove;
   }
   
   public Move getNextMove() {
      return bestMove;
   }
   
   public double getScore() {
      return score;
   }
   
   public static Move transform(Move m, int symmetryType) {
      switch (symmetryType) {
         case 0:
            return m;
         case 1:
            return new Move(m.getY(), m.getX());
         case 2:
            return new Move(7 - m.getX(), 7 - m.getY());
         case 3:
            return new Move(7 - m.getY(), 7 - m.getX());
      }
      // should never get here
      return null;
   }
   
   public String toString() {
      if (children.size() == 0) {
         return currMove.toString() + " " + score;
      } else {
         String ret = "";
         for (ankOpeningTree child : children) {
            ret += currMove.toString() + " " + child.toString();
            ret += "\n";
         }
         
         return ret;
      }
   }
}
