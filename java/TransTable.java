import java.util.Hashtable;
import java.util.Random;

/**
 * This class implements my own Transposition Table.
 * Currently, it is not integrated with any of the Othello players.
 * @author Arvind Vijayakumar
 */

public enum TransTable {
   INSTANCE;

   private HashMap<Long,Double> table = new HashMap<Long,Double>();
   
   public void addEntry(GameState gs, double score) {
      table.put(gs.getHashCode(), score);
   }
   
   public boolean hasEntry(GameState gs) {
      return table.containsKey(gs.getHashCode());
   }
   
   public double getEntry(GameState gs) {
      if(table.containsKey(gs.getHashCode()))
         return Double.NaN;
      return table.get(gs);
   }
}