package persistence.entity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import util.CloneUtils;

public class JdbcPersistenceContext implements PersistenceContext {

  private final Map<EntityKey, Object> entityCache = new ConcurrentHashMap<>();
  private final Map<EntityKey, Object> entitySnapshot = new ConcurrentHashMap<>();
  private final Map<Integer, EntityStatus> entityEntry = new ConcurrentHashMap<>();

  @Override
  public Optional<Object> getEntity(Long id, Class<?> clazz) {
    return Optional.ofNullable(entityCache.get(new EntityKey(clazz, id)));
  }

  @Override
  public <T> void removeEntity(T entity) {
    entityCache.entrySet().removeIf(entry -> entry.getValue().equals(entity));
    entitySnapshot.entrySet().removeIf(entry -> entry.getValue().equals(entity));
  }

  @Override
  public <T> void addEntity(Long id, T entity) {
    entityCache.put(new EntityKey(entity.getClass(), id), entity);
  }

  @Override
  public <T> void putDatabaseSnapshot(Long id, T entity) {
    entitySnapshot.put(new EntityKey(entity.getClass(), id), CloneUtils.clone(entity));
  }

  @Override
  public <T> boolean isSameWithSnapshot(Long id, T entity) {
    Object snapshot = entitySnapshot.get(new EntityKey(entity.getClass(), id));
    return entity.equals(snapshot);
  }

  @Override
  public List<Object> dirtyCheckedEntities() {
    return entityCache.entrySet()
        .stream()
        .filter(entry -> !isSameWithSnapshot(entry.getKey().getId(), entry.getValue().getClass()))
        .map(entry -> entry.getValue())
        .collect(Collectors.toList());
  }

  public void putEntityEntryStatus(Object entity, EntityStatus entityEntryStatus) {
    entityEntry.put(entity.hashCode(), entityEntryStatus);
  }

  @Override
  public EntityStatus getEntityStatus(Object entity) {
    return entityEntry.get(entity.hashCode());
  }

}
