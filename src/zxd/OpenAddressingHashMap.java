package zxd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import zxd.OAHashMapEntry.OAHashMapEntry_STATUS;

/*
 * Description: Open addressing hash map
 * Author: Zxd
 * Creation time: 2014/04/15
 */

public class OpenAddressingHashMap<K, V> implements Map<K, V> {

	private static final double ALPHA = 0.75;	// default fill ratio, must be less than 1.0
	private static int DEFAULT_CAPACITY = 100;
	
	private OAHashMapEntry<K, V>[] m_table = null;
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
		m_table = new OAHashMapEntry[m_capacity];
		for (int i=0; i<m_capacity; ++i) {
			m_table[i] = null;
		}
		
		// select hash function and pos conflict resolver
		m_hashFunc = new MurmurHash2();
		m_posDetector = new LinearPosDetector();
	}
	
	public OpenAddressingHashMap() {
		init(DEFAULT_CAPACITY);
	}
	
	public OpenAddressingHashMap(int capacity) {
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
			if (m_table[nextPos] != null && m_table[nextPos].m_status == OAHashMapEntry_STATUS.OCCUPIED
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
			
			if (m_table[nextPos] != null && m_table[nextPos].m_status == OAHashMapEntry_STATUS.OCCUPIED
					&& m_table[nextPos].m_key.equals(key)) {	// slot with same key
				return nextPos;
			} else if (m_table[nextPos].m_status == OAHashMapEntry_STATUS.REMOVED) {	// deleted slot
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
				m_table[pos].m_status == OAHashMapEntry_STATUS.OCCUPIED;
	}
	
	boolean isRemovedPos(int pos) {
		return pos >= 0 && pos < m_capacity && m_table[pos] != null && 
				m_table[pos].m_status == OAHashMapEntry_STATUS.REMOVED;
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
	public boolean containsKey(Object key) {
		int index = getIndex(key);
		return index >= 0;
	}

	@Override
	public boolean containsValue(Object value) {
		for (int i=0; i<m_table.length; ++i) {
			if (isOccupiedPos(i) && m_table[i].m_val.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		int index = getIndex(key);
		if (index < 0)
			return null;
		else {
			assert(isOccupiedPos(index) && m_table[index].m_key.equals(key));
			return m_table[index].m_val;
		}
	}

	@Override
	public V put(K key, V value) {
		int index = getInsertionIndex(key);
		if (index == -1) {
			return null;
		} 
		
		++m_size;
		if (m_table[index] == null) {
			m_table[index] = new OAHashMapEntry<K, V>(key, value);
			return null;	
		} else if (m_table[index].m_status == OAHashMapEntry_STATUS.REMOVED){
			m_table[index].setOccupied();
			m_table[index].setKeyVal(key, value);
			return null;
		} else {
			V oldVal = m_table[index].m_val;
			m_table[index].setOccupied();
			m_table[index].setKeyVal(key, value);
			return oldVal;	//return previous value
		}
	}

	@Override
	public V remove(Object key) {
		int index = getIndex(key);
		if (index == -1) {
			return null;
		}
		--m_size;
		assert(isOccupiedPos(index) && m_table[index].m_key.equals(key));
		V oldVal = m_table[index].m_val;
		m_table[index].setRemoved();
		return oldVal;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		for (int i=0; i<m_table.length; ++i) {
			if (m_table[i] != null)
				m_table[i].setRemoved();
		}
	}

	@Override
	public Set<K> keySet() {
		Set<K> res = new HashSet<K>();
		for (int i=0; i<m_table.length; ++i) {
			if (isOccupiedPos(i)) {
				res.add(m_table[i].m_key);
			}
		}
		return res;
		// TODO 看jdk文档要求：从keySet删除一个元素后，对应hashmap也自动删除了对应元素
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<Integer, Integer>(10);
		m.put(1, 1);
		m.put(2, 2);
		m.put(3, 3);
		m.put(4, 4);
		m.put(5, 5);
		m.put(6, 6);
		m.put(7, 7);
		m.put(8, 8);
		System.out.println("remove" + m.remove(3));
		System.out.println("remove" + m.remove(7));
		System.out.println("insert 9");
		m.put(9, 9);
		
		System.out.println(m.get(9));
		System.out.println(m.get(8));
		System.out.println(m.get(7));
		System.out.println(m.get(6));
		System.out.println(m.get(5));
		System.out.println(m.get(4));
		System.out.println(m.get(3));
		System.out.println(m.get(2));
		System.out.println(m.get(1));
		
		System.out.println(m.getDebugInfo());
	}

}
