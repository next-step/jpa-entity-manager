package jpa;

public interface PersistenceContext {

    void add(Object entity);

    Object get(EntityInfo<?> entityInfo);

    void remove(Object entity);

    boolean contain(EntityInfo<?> entityInfo);

    void update(Object entity);

}
