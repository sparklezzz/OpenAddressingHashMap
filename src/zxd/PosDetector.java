package zxd;

public abstract class PosDetector {
	
	/*
	 * return -1 if there is no extra valid pos, else return a valid pos between [0, capacity)
	 */
	public abstract int getPos(int hashed, int step, int capacity);
}
