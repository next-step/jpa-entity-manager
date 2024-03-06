package persistence.entity.database;

import database.mapping.ColumnValueMap;
import database.mapping.EntityClass;
import database.mapping.EntityMetadata;
import database.sql.dml.DeleteQueryBuilder;
import database.sql.dml.InsertQueryBuilder;
import database.sql.dml.UpdateQueryBuilder;
import jdbc.JdbcTemplate;

import java.util.Map;

/**
 * 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(Class<?> clazz, Object entity) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();

        Long id = metadata.getPrimaryKeyValue(entity);
        checkGenerationStrategy(metadata, id);
        id = metadata.requiresIdWhenInserting() ? id : null;
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(metadata)
                .id(id)
                .values(columnValues(entity));
        return jdbcTemplate.execute(insertQueryBuilder.toQueryString());
    }

    private void checkGenerationStrategy(EntityMetadata entityMetadata, Long id) {
        if (entityMetadata.requiresIdWhenInserting() && id == null) {
            throw new PrimaryKeyMissingException(entityMetadata.getEntityClassName());
        }
    }

    public void update(Class<?> clazz, Long id, Map<String, Object> changes) {
        doUpdate(clazz, id, changes);
    }

    public void update(Class<?> clazz, Long id, Object entity) {
        update(clazz, id, columnValues(entity));
    }

    private void doUpdate(Class<?> clazz, Long id, Map<String, Object> map) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();

        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(metadata);
        String query = updateQueryBuilder.buildQuery(id, map);
        jdbcTemplate.execute(query);
    }

    public void delete(Class<?> clazz, Long id) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();

        DeleteQueryBuilder deleteQueryBuilder1 = new DeleteQueryBuilder(metadata);
        String query = deleteQueryBuilder1.buildQuery(Map.of("id", id));
        jdbcTemplate.execute(query);
    }

    private Map<String, Object> columnValues(Object entity) {
        return ColumnValueMap.fromEntity(entity).getMap();
    }
}
