package persistence.model.meta;

public interface KeyValue<K, V> {
    K getKey();
    V getValue();
}
