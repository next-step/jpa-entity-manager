package persistence.entity;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcEntityManager implements EntityManager {
  private final Connection connection;
  private final Map<Class<?>, EntityPersister> persisterMap = new ConcurrentHashMap<>();

  public JdbcEntityManager(Connection connection) {
    this.connection = connection;
  }

  @Override
  public <T> T find(Class<T> clazz, Long id) {
    EntityPersister<T> persister = persisterMap.getOrDefault(clazz.getClass(),
        new JdbcEntityPersister<>(clazz, connection));
    persisterMap.putIfAbsent(clazz.getClass(), persister);

    return persister.load(id);
  }

  @Override
  public <T> void persist(T entity) {
    EntityPersister<T> persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister<>(entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

    if(persister.entityExists(entity)){
      persister.update(entity);
      return;
    }

    persister.insert(entity);
  }

  @Override
  public <T> void remove(T entity) {
    EntityPersister<T> persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister<>(entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

    persister.delete(entity);
  }

}
