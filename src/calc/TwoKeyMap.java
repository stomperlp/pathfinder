package calc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TwoKeyMap<K1, K2, V> {

    private final Map<CompositeKey<K1, K2>, V> map = new HashMap<>();

    public void put(K1 key1, K2 key2, V value) {
        map.put(new CompositeKey<>(key1, key2), value);
    }

    public V get(K1 key1, K2 key2) {
        return map.get(new CompositeKey<>(key1, key2));
    }

    public boolean containsKey(K1 key1, K2 key2) {
        return map.containsKey(new CompositeKey<>(key1, key2));
    }

    public V remove(K1 key1, K2 key2) {
        return map.remove(new CompositeKey<>(key1, key2));
    }
    public void clear() {
        map.clear();
    }
    public Collection<V> values(){
        return map.values();
    }

    private static class CompositeKey<K1, K2> {

        private final K1 key1;
        private final K2 key2;

        public CompositeKey(K1 key1, K2 key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompositeKey<?, ?> that = (CompositeKey<?, ?>) o;
            return Objects.equals(key1, that.key1) && 
                   Objects.equals(key2, that.key2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key1, key2);
        }
    }
}
