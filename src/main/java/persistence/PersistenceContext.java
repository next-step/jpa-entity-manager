package persistence;

public interface PersistenceContext {

    Object findEntity(EntityInfo<?> entityObject);

    void updateEntity(EntityInfo<?> entityObject, Object object);

    void insertEntity(EntityInfo<?> entityObject, Object object);

    void deleteEntity(EntityInfo<?> entityObject);

}
