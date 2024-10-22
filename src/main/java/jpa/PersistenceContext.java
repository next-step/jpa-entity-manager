package jpa;

public interface PersistenceContext {

    void add(EntityInfo<?> entityInfo, Object entity);

    Object get(EntityInfo<?> entityInfo);

    void remove(EntityInfo<?> entityInfo);

}
