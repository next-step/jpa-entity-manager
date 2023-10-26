package hibernate.entity.meta;

import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.meta.column.EntityColumns;
import jakarta.persistence.Entity;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityClass<T> {

    private static final Map<Class<?>, EntityClass<?>> CACHE = new ConcurrentHashMap<>();

    private final EntityTableName tableName;
    private final EntityColumns entityColumns;
    private final Class<T> clazz;

    private EntityClass(final Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Entity 어노테이션이 없는 클래스는 입력될 수 없습니다.");
        }
        this.tableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz.getDeclaredFields());
        this.clazz = clazz;
    }

    public static <T> EntityClass<T> getInstance(final Class<T> clazz) {
        return (EntityClass<T>) CACHE.computeIfAbsent(clazz, EntityClass::new);
    }

    public T newInstance()  {
        Constructor<T> constructor = getConstructor();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("생성자에 접근할 수 없습니다.");
        } catch (Exception e) {
            throw new IllegalStateException("생성자 생성에 문제가 발생했습니다.", e);
        } finally {
            constructor.setAccessible(false);
        }
    }

    private Constructor<T> getConstructor() {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("기본 생성자가 존재하지 않습니다.");
        }
    }

    public Map<EntityColumn, Object> getFieldValues(final Object entity) {
        return entityColumns.getFieldValues(entity);
    }

    public String tableName() {
        return tableName.getValue();
    }

    public EntityColumn getEntityId() {
        return entityColumns.getEntityId();
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns.getValues();
    }

    public List<String> getFieldNames() {
        return entityColumns.getFieldNames();
    }
}
