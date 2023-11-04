package persistence.entity;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcEntityManager implements EntityManager {
  private final Connection connection;
  private final Map<Class<?>, EntityPersister> persisterMap = new ConcurrentHashMap<>();
  private final Map<Class<?>, EntityLoader> loaderMap = new ConcurrentHashMap<>();


  public JdbcEntityManager(Connection connection) {
    this.connection = connection;
  }

  @Override
  public <T> T find(Class<T> clazz, Long id) {
    EntityLoader loader = loaderMap.getOrDefault(clazz.getClass(), new JdbcEntityLoader<>(clazz, connection));
    loaderMap.putIfAbsent(clazz, loader);

    return loader.load(id);
  }

  @Override
  public void persist(Object entity) {
    EntityPersister persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister(entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

    persister.insert(entity);
  }

  @Override
  public void remove(Object entity) {
    EntityPersister persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister(entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

    persister.delete(entity);
  }

}
