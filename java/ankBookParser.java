/*
 * ankBookParser.java
 *
 * Created on March 8, 2005, 3:10 PM
 */

import java.io.*;
import java.util.*;

/**
 *
 * @author Andrew N. Kwok
 */
public class ankBookParser {
   
   private ankOpeningTree root;
   
   /** Creates a new instance of ankBookParser */
   public ankBookParser(ankOpeningTree root, String fileLoc) {
      this.root = root;
      
      String moveString = "";
      String moveList = "";
      double moveScore = 0;
      
      try {
         BufferedReader br = new BufferedReader(new FileReader(fileLoc));
         
         LinkedList<Move> moves;
         
         while (br.ready()) {
            moveString = br.readLine();
            moveList = moveString.substring(0, moveString.indexOf('X'));
            moveScore = Double.parseDouble(moveString.substring(
                    moveString.indexOf('X') + 1, moveString.length()));
            moves = getMoveList(moveList);
            root.addOpening(moves, moveScore, root);
         }
      } catch (Exception e) {
         System.out.println("At opening " + moveString);
         e.printStackTrace();
      }
   }
   
   public LinkedList<Move> getMoveList(String moves) {
      LinkedList<Move> movesList = new LinkedList<Move>();
      int numMoves = moves.length() / 2;
      int moveX;
      int moveY;
      String move;
      
      for (int i = 0; i < numMoves; i++) {
         move = moves.substring(2 * i, 2 * i + 2).toUpperCase();
         moveX = ((int) move.charAt(0)) - 65;
         moveY = ((int) move.charAt(1)) - 49;
         movesList.add(new Move(moveX, moveY));
      }
      
      return movesList;
   }
   
   public ankOpeningTree getTree() {
      return root;
   }
   
   public static void main(String[] args) {
      ankOpeningTree tree = new ankOpeningTree();
      ankBookParser parse = new ankBookParser(tree, "ankBook.txt");
      System.out.println(tree.searchScores(ankBoard.WHITE, tree));
      
//      tree = tree.getOpening(new Move(4,2));
//      tree = tree.getOpening(new Move(5,3));
//      tree = tree.getOpening(new Move(2,4));
//      tree = tree.getOpening(new Move(3,5));
//      tree = tree.getOpening(new Move(5,2));
//      tree = tree.getOpening(new Move(4,5));
//      tree = tree.getOpening(new Move(2,2));
//      tree = tree.getOpening(new Move(3,2));
//      tree = tree.getOpening(new Move(4,1));
//      System.out.println(tree.getNextMove());
      
      while(tree.getNextMove() != null) {
         System.out.println(tree.getNextMove());
         tree = tree.getOpening(tree.getNextMove());
      }
   }
}
