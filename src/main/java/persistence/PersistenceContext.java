package persistence;

public interface PersistenceContext {

    Object findEntity(EntityInfo<?> entityObject);

    void insertEntity(EntityInfo<?> entityObject, Object object);

    void deleteEntity(EntityInfo<?> entityObject);

}
