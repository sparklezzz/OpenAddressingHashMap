package zxd;

public final class MurmurHash2 extends HashFunction {
	
	public int hash(byte[] data, int seed, int offset, int len) {
		int m = 0x5bd1e995;
		int r = 24;
		int h = seed ^ len;
		int len_4 = len >> 2;
		for (int i = 0; i < len_4; i++) {
			int i_4 = offset + (i << 2);
			int k = data[i_4 + 3];
			k = k << 8;
			k = k | (data[i_4 + 2] & 0xff);
			k = k << 8;
			k = k | (data[i_4 + 1] & 0xff);
			k = k << 8;
			k = k | (data[i_4 + 0] & 0xff);
			k *= m;
			k ^= k >>> r;
			k *= m;
			h *= m;
			h ^= k;
		}
		int len_m = len_4 << 2;
		int left = len - len_m;
		if (left != 0) {
			if (left >= 3) {
				h ^= data[offset + len - 3] << 16;
			}
			if (left >= 2) {
				h ^= data[offset + len - 2] << 8;
			}
			if (left >= 1) {
				h ^= data[offset + len - 1];
			}
			h *= m;
		}
		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;
		return h;
	}

	/**
	 * Generates 32 bit hash from byte array with default seed value.
	 * 
	 * @param data
	 *            byte array to hash
	 * @param offset
	 *            the start position in the array to hash
	 * @param len
	 *            length of the array elements to hash
	 * @return 32 bit hash of the given array
	 */
	public int hash32(final byte[] data, int offset, int len) {
		return hash(data, 0x9747b28c, offset, len);
	}

	@Override
	public int hash(byte[] bytes) {
		return hash32(bytes, 0, bytes.length);
	}

	@Override
	public final int hash(int res) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (res & 0xff);// 最低位 
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位 
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位 
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		
		return hash32(targets, 0, targets.length);
	}
}
