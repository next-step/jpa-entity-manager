package persistence.entity;

import database.sql.Person;
import database.sql.dml.QueryBuilder;
import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.HashMap;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    public static final RowMapper<Person> PERSON_ROW_MAPPER = resultSet -> new Person(
            resultSet.getLong("id"),
            resultSet.getString("nick_name"),
            resultSet.getInt("old"),
            resultSet.getString("email"));

    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, EntityPersister> entityPersisters;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        entityPersisters = new HashMap<>();
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        String query = QueryBuilder.getInstance().buildSelectOneQuery(clazz, Id);
        return (T) jdbcTemplate.queryForObject(query, PERSON_ROW_MAPPER); // 여기도 하드코딩
    }

    @Override
    public void persist(Object entity) {
        EntityPersister entityPersister = getEntityPersister(entity.getClass());

        if (getId(entity) == 0) {
            entityPersister.insert(entity);
            return;
        }
        // XXX: 테스트 안됨
        entityPersister.update(entity);
    }

    @Override
    public void remove(Object entity) {
        getEntityPersister(entity.getClass()).delete(entity);
    }

    private EntityPersister getEntityPersister(Class<?> entityClass) {
        if (!entityPersisters.containsKey(entityClass)) {
            entityPersisters.put(entityClass, new EntityPersister(jdbcTemplate, entityClass));
        }
        return entityPersisters.get(entityClass);
    }

    private static long getId(Object entity) {
        EntityMetadata metadata = new EntityMetadata(entity.getClass());
        return metadata.getPrimaryKeyValue(entity);
    }
}
