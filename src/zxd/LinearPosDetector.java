package zxd;

public class LinearPosDetector extends PosDetector {
	@Override
	public int getPos(int hashed, int step, int capacity) {
		int pos = (hashed + step) % capacity;
		return pos < 0 ? pos + capacity : pos;
	}
}
