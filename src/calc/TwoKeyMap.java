package calc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A map implementation that uses two keys (K1 and K2) to store and retrieve values.
 * Internally uses a composite key pattern to combine both keys into a single key.
 *
 * @param <K1> Type of the first key
 * @param <K2> Type of the second key
 * @param <V>  Type of the stored values
 */
public class TwoKeyMap<K1, K2, V> {

    // Internal storage using composite keys
    private final Map<CompositeKey<K1, K2>, V> map = new HashMap<>();

    /**
     * Associates the specified value with the specified keys in this map.
     * @param key1  the first key
     * @param key2  the second key
     * @param value the value to be associated with the keys
     */
    public void put(K1 key1, K2 key2, V value) {
        map.put(new CompositeKey<>(key1, key2), value);
    }

    /**
     * Returns the value associated with the specified keys.
     * @param key1 the first key
     * @param key2 the second key
     * @return the value associated with the keys, or null if no mapping exists
     */
    public V get(K1 key1, K2 key2) {
        return map.get(new CompositeKey<>(key1, key2));
    }

    /**
     * Checks if the map contains a mapping for the specified keys.
     * @param key1 the first key
     * @param key2 the second key
     * @return true if the map contains a mapping for the keys
     */
    public boolean containsKey(K1 key1, K2 key2) {
        return map.containsKey(new CompositeKey<>(key1, key2));
    }

    /**
     * Removes the mapping for the specified keys from the map.
     * @param key1 the first key
     * @param key2 the second key
     * @return the previous value associated with the keys, or null if no mapping existed
     */
    public V remove(K1 key1, K2 key2) {
        return map.remove(new CompositeKey<>(key1, key2));
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns a collection view of the values contained in this map.
     * @return a collection view of the values
     */
    public Collection<V> values(){
        return map.values();
    }

    /**
     * Internal class representing a composite key made of two keys (K1 and K2).
     * Implements proper equals() and hashCode() for use as a map key.
     *
     * @param <K1> Type of the first key
     * @param <K2> Type of the second key
     */
    private static class CompositeKey<K1, K2> {

        private final K1 key1;
        private final K2 key2;

        /**
         * Creates a new composite key from two individual keys.
         * @param key1 the first key
         * @param key2 the second key
         */
        public CompositeKey(K1 key1, K2 key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        /**
         * Compares this composite key with another object for equality.
         * @param o the object to compare with
         * @return true if the other object is a CompositeKey with equal component keys
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompositeKey<?, ?> that = (CompositeKey<?, ?>) o;
            return Objects.equals(key1, that.key1) && 
                   Objects.equals(key2, that.key2);
        }

        /**
         * Returns a hash code value for this composite key.
         * @return a hash code value based on both component keys
         */
        @Override
        public int hashCode() {
            return Objects.hash(key1, key2);
        }
    }
}