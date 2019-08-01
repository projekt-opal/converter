package org.diceresearch.common.utility;

import java.io.Serializable;

public class Pair<K, V> implements Serializable {

    private static final long serialVersionUID = 8568411150824768634L;

    private K key;
    private V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public Pair() { //must exist for deserialization
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public String toString() {
        return "(" + key + ", " + value + ")";
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 17 * result + ((key == null) ? 0 : key.hashCode());
        result = 17 * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Pair))
            return false;
        Pair other = (Pair) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (value == null) {
            return other.value == null;
        } else return value.equals(other.value);
    }
}
