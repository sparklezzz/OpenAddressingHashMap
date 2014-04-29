package zxd;

public abstract class HashFunction {
/**
   * Hashes the contents of the referenced bytes
   * @param bytes the data to be hashed
   * @return the hash of the bytes referenced by bytes.offset and length bytes.length
   */
  public abstract int hash(byte[] bytes);

  public abstract int hash(int num);
  
}
