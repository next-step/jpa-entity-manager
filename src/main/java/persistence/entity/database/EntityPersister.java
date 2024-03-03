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
        id = metadata.hasIdGenerationStrategy() ? null : id;
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(metadata)
                .id(id)
                .values(columnValues(entity));
        Long generatedId = jdbcTemplate.execute(insertQueryBuilder.toQueryString());
        return generatedId;
    }

    /**
     * id 없고 strategy 있고 --> 무시하거나 예외 내야 함
     */
    private void checkGenerationStrategy(EntityMetadata entityMetadata, Long id) {
        // XXX: GenerationType 에 따라서 분기 칠 수 있을까?
        // id 가 필요한지 안 필요한지를 메타데이터에 물어보면 좋을듯
        boolean hasIdGenerationStrategy = entityMetadata.hasIdGenerationStrategy();
        if (!hasIdGenerationStrategy && id == null) {
            throw new PrimaryKeyMissingException();
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
