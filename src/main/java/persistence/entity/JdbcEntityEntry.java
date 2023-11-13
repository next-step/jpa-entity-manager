package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcEntityEntry implements EntityEntry{
  private final Map<Integer, EntityStatus> entityEntry = new ConcurrentHashMap<>();

  @Override
  public void putEntityEntryStatus(Object entity, EntityStatus entityEntryStatus) {
    entityEntry.put(entity.hashCode(), entityEntryStatus);
  }

  @Override
  public EntityStatus getEntityStatus(Object entity) {
    return entityEntry.get(entity.hashCode());
  }
}
