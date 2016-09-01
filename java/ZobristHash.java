import java.util.Random;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * This class implements my own Zobrist Hash.
 * Currently, it is not integrated with any of the Othello players.
 * @author Arvind Vijayakumar
 */

public enum ZobristHash {
	INSTANCE;
	
	long[][] pos = new long[2][64];
	
	long board;
	
	public final int BLACK = 0;
	public final int WHITE = 1;
	
	
	private ZobristHash() {
		SecureRandom sr = new SecureRandom();
		for(int side = 0; side < 2; side++) {
			for(int x = 0; x < 8; x++) {
				for(int y = 0; y < 8; y++) {
					byte[] bytes = new byte[8];
					sr.nextBytes(bytes);
					ByteBuffer bb = ByteBuffer.wrap(bytes);
					pos[side][8*x + y] = bb.getLong();
				}
			}
		}
		byte[] bytes = new byte[8];
		sr.nextBytes(bytes);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		board = bb.getLong();
	}
	
	public void regenerateHash() {
		SecureRandom sr = new SecureRandom();
		for(int side = 0; side < 2; side++) {
			for(int x = 0; x < 8; x++) {
				for(int y = 0; y < 8; y++) {
					byte[] bytes = new byte[8];
					sr.nextBytes(bytes);
					ByteBuffer bb = ByteBuffer.wrap(bytes);
					pos[side][8*x + y] = bb.getLong();
				}
			}
		}
		byte[] bytes = new byte[8];
		sr.nextBytes(bytes);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		board = bb.getLong();
	}	
	
}
