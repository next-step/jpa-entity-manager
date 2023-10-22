package persistence.entity;

import jdbc.RowMapper;

import java.util.List;

public interface EntityManager<T> extends RowMapper<T> {
    List<T> findAll();
    <R> T find(R r);

    Object persist(Object entity);

    void remove(Object entity);
}
