package persistence.entity;

import database.sql.dml.DeleteQueryBuilder;
import database.sql.dml.InsertQueryBuilder;
import database.sql.dml.UpdateQueryBuilder;
import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;

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
        this.insertQueryBuilder = new InsertQueryBuilder(entityClass);
        this.updateQueryBuilder = new UpdateQueryBuilder(entityClass);
        this.deleteQueryBuilder = new DeleteQueryBuilder(entityClass);
    }

    public void insert(Object entity) {
        String query = insertQueryBuilder.buildQuery(entity);
        jdbcTemplate.execute(query);
    }

    public boolean update(Object entity) {
        String query = updateQueryBuilder.buildQuery(getId(entity), entity);
        jdbcTemplate.execute(query);
        return true;

        // TODO: return false 상황이 있을텐데? update 실패의 상황을 어떻게 캐치할 수 있을까(affected row 개수를 보고 싶은데)
    }

    public void delete(Object entity) {
        String query = deleteQueryBuilder.buildQuery(Map.of("id", getId(entity)));
        jdbcTemplate.execute(query);
    }

    private Long getId(Object entity) {
        return entityMetadata.getPrimaryKeyValue(entity);
    }
}
