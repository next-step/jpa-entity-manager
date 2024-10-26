package persistence;

public interface PersistenceContext {

    Object findEntity(EntityKey<?> entityKey);

    void insertEntity(EntityKey<?> entityKey, Object object);

    void deleteEntity(EntityKey<?> entityKey);

    void insertDatabaseSnapshot(EntityKey<?> entityKey, Object object);

    Object getDatabaseSnapshot(EntityKey<?> entityKey);

}
