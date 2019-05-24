package dataAccess

//LRU cache
class Cache[K,V](capacity:Int) extends java.util.LinkedHashMap[K, V]{
  override def removeEldestEntry(eldest: java.util.Map.Entry[K,V]): Boolean = size > capacity
}
