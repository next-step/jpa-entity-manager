package persistence.entity;

import java.util.List;
import java.util.Map;

public interface EntityManager {

    <T> List<T> findAll(Class<T> tClass);

    <T, R> T find(Class<T> tClass, R r);

    <T> T persist(T t);

    <T> void remove(T t, Object arg);

    <T> void update(T t, Object arg);

    void flush();
}
