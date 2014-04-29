package zxd;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import zxd.OAHashSetEntry.OAHashSetEntry_STATUS;
import zxd.OAHashSetEntry.OAHashSetEntry_STATUS;

public class OpenAddressingHashSet<E> implements Set<E> {

	private static final double ALPHA = 0.75;	// default fill ratio, must be less than 1.0
	private static int DEFAULT_CAPACITY = 100;
	
	private OAHashSetEntry<E>[] m_table = null;
	private int m_capacity = 0;
	private int m_size = 0;
	HashFunction m_hashFunc = null;
	PosDetector m_posDetector = null;
	
	public String getDebugInfo() {
		String res = "Capacity: " + m_capacity + "\n";
		res += "Size: " + m_size + "\n";
		for (int i=0; i<m_capacity; ++i) {
			res += i + ": ";
			res += m_table[i] == null ? "null" : m_table[i].toString();
			res += "\n";
		}
		return res;
	}
	
	public void init(int capacity) {
		m_capacity = capacity;
		m_size = 0;
		m_table = new OAHashSetEntry[m_capacity];
		for (int i=0; i<m_capacity; ++i) {
			m_table[i] = null;
		}
		
		// select hash function and pos conflict resolver
		m_hashFunc = new MurmurHash2();
		m_posDetector = new LinearPosDetector();
	}
	
	public OpenAddressingHashSet() {
		init(DEFAULT_CAPACITY);
	}
	
	public OpenAddressingHashSet(int capacity) {
		init(capacity);
	}
	
	private int getHashedKey(Object key) {
		return m_hashFunc.hash(key.hashCode());
	}
	
	private int getIndex(Object key) {
		int hashed = getHashedKey(key); 
		int step = 0;
		int nextPos;
		do {
			nextPos = m_posDetector.getPos(hashed, step, m_capacity);
			if (m_table[nextPos] != null && m_table[nextPos].m_status == OAHashSetEntry_STATUS.OCCUPIED
					&& m_table[nextPos].m_key.equals(key)) {
				return nextPos;
			}
			// deleted slot or unequal-key slot, continue
			++step;
		} while (m_table[nextPos] != null);
		// empty slot
		return -1;
	}
	
	private int getInsertionIndex(Object key) {
		int hashed = getHashedKey(key);
		int step = 0;
		int nextPos = m_posDetector.getPos(hashed, step, m_capacity);
	
		int firstRemovedPos = -1;
		while (m_table[nextPos] != null){	// stop till we encounter an empty slot
			
			if (m_table[nextPos] != null && m_table[nextPos].m_status == OAHashSetEntry_STATUS.OCCUPIED
					&& m_table[nextPos].m_key.equals(key)) {	// slot with same key
				return nextPos;
			} else if (m_table[nextPos].m_status == OAHashSetEntry_STATUS.REMOVED) {	// deleted slot
				if (firstRemovedPos == -1) {
					firstRemovedPos = nextPos;
				}
			}
			// unequal-key slot, step forward
			++step;
			nextPos = m_posDetector.getPos(hashed, step, m_capacity);
		} 	
		
		if (firstRemovedPos == -1) {	// haven't met removed slot, return the emply slot which nextPos points to
			return nextPos;
		} else {	// reuse first removed pos
			return firstRemovedPos;
		}
	}
	
	boolean isOccupiedPos(int pos) {
		return pos >= 0 && pos < m_capacity && m_table[pos] != null && 
				m_table[pos].m_status == OAHashSetEntry_STATUS.OCCUPIED;
	}
	
	boolean isRemovedPos(int pos) {
		return pos >= 0 && pos < m_capacity && m_table[pos] != null && 
				m_table[pos].m_status == OAHashSetEntry_STATUS.REMOVED;
	}
	
	@Override
	public int size() {
		return m_size;
	}

	@Override
	public boolean isEmpty() {
		return m_size == 0;
	}

	@Override
	public boolean contains(Object o) {
		int index = getIndex(o);
		return index >= 0;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(E e) {	// we all null value here
		int index = getInsertionIndex(e);
		assert (index != -1);
		
		++m_size;
		if (m_table[index] == null) {
			m_table[index] = new OAHashSetEntry<E>(e);
			return true;	
		} else if (m_table[index].m_status == OAHashSetEntry_STATUS.REMOVED){
			m_table[index].setOccupied();
			m_table[index].setKey(e);
			return true;
		} else {	// slot with same key
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		int index = getIndex(o);
		if (index == -1) {	// not contain the key
			return false;
		}
		
		--m_size;
		assert(isOccupiedPos(index) && m_table[index].m_key.equals(o));
		m_table[index].setRemoved();
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
