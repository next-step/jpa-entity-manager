package persistence.entity.database;

import database.sql.dml.ColumnValueMap;
import database.sql.dml.DeleteQueryBuilder;
import database.sql.dml.InsertQueryBuilder;
import database.sql.dml.UpdateQueryBuilder;
import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;
import persistence.entity.data.EntitySnapshotDifference;

import java.util.Map;

/**
 * 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMetadata entityMetadata;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<?> entityClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetadata = new EntityMetadata(entityClass);
        this.insertQueryBuilder = new InsertQueryBuilder(entityMetadata);
        this.updateQueryBuilder = new UpdateQueryBuilder(entityMetadata);
        this.deleteQueryBuilder = new DeleteQueryBuilder(entityMetadata);
    }

    public void insert(Object entity) {
        String query = insertQueryBuilder.buildQuery(entity);
        jdbcTemplate.execute(query);
    }

    public void update(Long id, EntitySnapshotDifference difference) {
        doUpdate(id, difference.toMap());
    }

    public void update(Long id, Object entity) {
        doUpdate(id, new ColumnValueMap(entity).getColumnValueMap());
    }

    private void doUpdate(Long id, Map<String, Object> map) {
        String query = updateQueryBuilder.buildQuery(id, map);
        jdbcTemplate.execute(query);
    }

    public void delete(Long id) {
        String query = deleteQueryBuilder.buildQuery(Map.of("id", id));
        jdbcTemplate.execute(query);
    }
}