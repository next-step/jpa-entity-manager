package persistence;

public interface PersistenceContext {

    Object findEntity(EntityKey<?> entityObject);

    void insertEntity(EntityKey<?> entityObject, Object object);

    void deleteEntity(EntityKey<?> entityObject);

}
