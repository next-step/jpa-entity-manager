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

  public JdbcEntityManager(Connection connection, PersistenceContext persistenceContext) {
    this.connection = connection;
    this.persistenceContext = persistenceContext;
  }

  @Override
  public <T> T find(Class<T> clazz, Long id) {
    EntityPersister<T> persister = persisterMap.getOrDefault(clazz,
        new JdbcEntityPersister<>(clazz, connection));
    persisterMap.putIfAbsent(clazz, persister);

    return (T) persistenceContext.getEntity(id, clazz)
        .orElseGet(() -> {
          Optional<T> entity = persister.load(id);

          entity.ifPresent(presentEntity -> {
            putPersistenceContext(id, presentEntity);
          });

          if (entity.isPresent()) {
            return entity.get();
          }

          return null;
        });
  }

  @Override
  public <T> void persist(T entity) {
    EntityPersister<T> persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister<>((Class<T>) entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

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

    persistenceContext.removeEntity(entity);
    persister.delete(entity);
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
  }
}
