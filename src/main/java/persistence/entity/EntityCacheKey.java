package persistence.entity;

import java.util.Objects;

/***
 * Entity Caching 을 위한 Key
 * Entity Clazz : Entity 타입
 * Id : 해당 Entity의 식별자
 */
public class EntityCacheKey {

    private final Class<?> entityClazz;

    private final String id;

    public EntityCacheKey(Class<?> entityClazz, Object id) {
        this.entityClazz = entityClazz;
        this.id = String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityCacheKey)) return false;
        EntityCacheKey that = (EntityCacheKey) o;
        return Objects.equals(entityClazz, that.entityClazz) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClazz, id);
    }
}
