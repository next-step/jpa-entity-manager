package persistence.entity;

import java.util.Objects;

public class EntityKey {

  private final Class<?> clazz;
  private final Long id;

  public EntityKey(Class<?> clazz, Long id) {
    this.clazz = clazz;
    this.id = id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(clazz, id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityKey entityKey = (EntityKey) o;
    return Objects.equals(clazz, entityKey.clazz) && Objects.equals(id,
        entityKey.id);
  }
}
