package persistence.entity.persistencecontext;

public interface PersistenceContext<E> {
    E getEntity(EntityPersistIdentity id);

    void addEntity(EntityPersistIdentity id, E entity);

    void removeEntity(E entity);

    EntitySnapShot getDatabaseSnapshot(EntityPersistIdentity id, E entity);

    void addEntityEntry(EntityPersistIdentity id, EntityEntry entityEntry);
    EntityEntry getEntityEntry(EntityPersistIdentity id);
}
