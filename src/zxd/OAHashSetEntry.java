package zxd;

public class OAHashSetEntry<K> {
	public enum OAHashSetEntry_STATUS {
		OCCUPIED,
		REMOVED,
	}
	
	public K m_key;
	public OAHashSetEntry_STATUS m_status;
	
	public OAHashSetEntry() {
		setOccupied();
		setKey(null);
	}
	
	public OAHashSetEntry(K key) {
		setOccupied();
		setKey(key);
	}
	
	public void setRemoved() {
		m_status = OAHashSetEntry_STATUS.REMOVED;
	}
	
	public void setOccupied() {
		m_status = OAHashSetEntry_STATUS.OCCUPIED;
	}
	
	public void setKey(K key) {
		m_key = key;
	}
	
	@Override
	public String toString() {
		String statusStr = m_status == OAHashSetEntry_STATUS.OCCUPIED ? "OCCUPIED" : "REMOVED";
		String res = "Status: " + statusStr + " Key: " + m_key.toString();
		return res;
	}
}