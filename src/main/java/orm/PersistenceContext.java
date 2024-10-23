package orm;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object object);

    boolean containsEntity(EntityKey key);

    void removeEntity(EntityKey entityKey);

    void clear();
}
