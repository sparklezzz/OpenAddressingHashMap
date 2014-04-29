package zxd;

public class OAHashMapEntry<K, V> {
	public enum OAHashMapEntry_STATUS {
		OCCUPIED,
		REMOVED,
	}
	
	public K m_key;
	public V m_val;
	public OAHashMapEntry_STATUS m_status;
	
	public OAHashMapEntry() {
		setOccupied();
		setKeyVal(null, null);
	}
	
	public OAHashMapEntry(K key, V val) {
		setOccupied();
		setKeyVal(key, val);
	}
	
	public void setRemoved() {
		m_status = OAHashMapEntry_STATUS.REMOVED;
	}
	
	public void setOccupied() {
		m_status = OAHashMapEntry_STATUS.OCCUPIED;
	}
	
	public void setKeyVal(K key, V val) {
		m_key = key;
		m_val = val;
	}
	
	@Override
	public String toString() {
		String statusStr = m_status == OAHashMapEntry_STATUS.OCCUPIED ? "OCCUPIED" : "REMOVED";
		String res = "Status: " + statusStr + " Key: " + m_key.toString() + " Val: " + m_val.toString();
		return res;
	}
}