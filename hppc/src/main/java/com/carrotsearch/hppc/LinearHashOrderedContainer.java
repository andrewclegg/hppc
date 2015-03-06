package com.carrotsearch.hppc;

/**
 * All associative containers that use hash-based distribution of keys and
 * linear addressing must implement this marker interface. Adding elements from
 * two such containers may result in practical deadlocks resulting from the
 * worst possible key ordering (conflict avalanche).
 *
 * For more discussion and a potential resolution when doing custom loops, see
 * <a href="http://issues.carrot2.org/browse/HPPC-103">HPPC-103</a>.
 * 
 * @see ObjectObjectMap#putAll(Iterable)
 * @see ObjectSet#putAll(Iterable)
 */
public interface LinearHashOrderedContainer {
  /**
   * Number of key slots used in internal hash-based 
   * distribution storage.
   */
  int keySlots();
}
