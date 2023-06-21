package persistence.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.sql.ddl.ReflectiveRowMapper;
import persistence.sql.ddl.SelectQueryBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityManagerImpl implements EntityManager {
    private final transient StatefulPersistenceContext persistenceContext;
    private final SelectQueryBuilder selectQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    private EntityManagerImpl(StatefulPersistenceContext persistenceContext, SelectQueryBuilder selectQueryBuilder, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.selectQueryBuilder = selectQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public EntityManagerImpl(SelectQueryBuilder selectQueryBuilder, JdbcTemplate jdbcTemplate) {
        this(initStatefulPersistenceContext(), selectQueryBuilder, jdbcTemplate);
    }

    private static StatefulPersistenceContext initStatefulPersistenceContext() {
        return new StatefulPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Long key) {
        EntityKey entityKey = EntityKey.of(key, clazz.getSimpleName());
        if (persistenceContext.containsEntity(entityKey)) {
            return clazz.cast(persistenceContext.getEntity(entityKey));
        }

        String sql = selectQueryBuilder.findById(clazz.getSimpleName(), unique(clazz.getDeclaredFields()).getName(), String.valueOf(key));
        ReflectiveRowMapper<T> mapper = new ReflectiveRowMapper<>(clazz);

        Object object = jdbcTemplate.queryForObject(sql, mapper);
        persistenceContext.addEntity(entityKey, object);

        return clazz.cast(persistenceContext.getEntity(entityKey));
    }

    @Override
    public void persist(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);
        EntityKey entityKey = EntityKey.of((Long) key, clazz.getSimpleName());

        persistenceContext.addEntity(entityKey, entity);
    }

    @Override
    public void remove(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);
        EntityKey entityKey = EntityKey.of((Long) key, clazz.getSimpleName());

        persistenceContext.removeEntity(entityKey);
    }


    @Override
    public boolean contains(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);

        return persistenceContext.containsEntity(EntityKey.of((Long) key, clazz.getSimpleName()));
    }

    private Field unique(Field[] field) {
        return Arrays.stream(field)
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Object getKey(Object entity) throws IllegalAccessException {
        Field keyField = unique(entity.getClass().getDeclaredFields());
        keyField.setAccessible(true);
        Object key = keyField.get(entity);
        return key;
    }
}
