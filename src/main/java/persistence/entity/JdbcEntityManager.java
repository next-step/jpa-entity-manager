package persistence.entity;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcEntityManager implements EntityManager {

  private final Connection connection;
  private final Map<Class<?>, EntityPersister> persisterMap = new ConcurrentHashMap<>();
  private final PersistenceContext persistenceContext;
  private final EntityEntry entityEntry;

  public JdbcEntityManager(Connection connection, PersistenceContext persistenceContext,
      EntityEntry entityEntry) {
    this.connection = connection;
    this.persistenceContext = persistenceContext;
    this.entityEntry = entityEntry;
  }

  @Override
  public <T> T find(Class<T> clazz, Long id) {
    EntityPersister<T> persister = persisterMap.getOrDefault(clazz,
        new JdbcEntityPersister<>(clazz, connection));
    persisterMap.putIfAbsent(clazz, persister);

    Optional<T> entity = (Optional<T>) persistenceContext.getEntity(id, clazz);

    entity.ifPresent(obj -> entityEntry.putEntityEntryStatus(obj, EntityStatus.LOADING));

    T foundEntity = entity.orElseGet(() -> {
      Optional<T> obj = persister.load(id);
      obj.ifPresent(presentEntity ->
          putPersistenceContext(id, presentEntity));

      return obj.orElse(null);
    });

    return foundEntity;
  }

  @Override
  public <T> void persist(T entity) {
    EntityPersister<T> persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister<>((Class<T>) entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);
    entityEntry.putEntityEntryStatus(entity, EntityStatus.SAVING);
    Long assignedId = persister.getEntityId(entity).orElse(-1L);

    if (persister.entityExists(entity) &&
        !persistenceContext.isSameWithSnapshot(assignedId, entity)) {

      persister.update(entity);
      putPersistenceContext(assignedId, entity);
      return;
    }

    Long id = persister.insert(entity);
    putPersistenceContext(id, entity);
  }

  @Override
  public <T> void remove(T entity) {
    EntityPersister<T> persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister<>((Class<T>) entity.getClass(), connection));

    persisterMap.putIfAbsent(entity.getClass(), persister);

    removePersistenceContext(entity);

    deleteEntity(persister, entity);
  }

  @Override
  public void sync() {
    List<Object> entites = persistenceContext.dirtyCheckedEntities();
    entites
        .forEach(this::persist);
  }

  public <T> void putPersistenceContext(Long id, T entity) {
    persistenceContext.addEntity(id, entity);
    persistenceContext.putDatabaseSnapshot(id, entity);
    entityEntry.putEntityEntryStatus(entity, EntityStatus.MANAGED);
  }
  public <T> void removePersistenceContext(T entity) {
    entityEntry.putEntityEntryStatus(entity, EntityStatus.DELETED);
    persistenceContext.removeEntity(entity);
  }

  public <T> void deleteEntity(EntityPersister<T> persister, T entity) {
    persister.delete(entity);
    entityEntry.putEntityEntryStatus(entity, EntityStatus.GONE);
  }

}
