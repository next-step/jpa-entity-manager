package persistence.entity;

import database.sql.dml.SelectByPrimaryKeyQueryBuilder;
import database.sql.dml.SelectQueryBuilder;
import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final String tableName;
    private final SelectByPrimaryKeyQueryBuilder selectByPrimaryKeyQueryBuilder;
    private final SelectQueryBuilder selectQueryBuilder;
    private final RowMapper<Object> rowMapper;

    public EntityLoader(JdbcTemplate jdbcTemplate, Class<?> entityClass) {
        this.jdbcTemplate = jdbcTemplate;
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        this.tableName = entityMetadata.getTableName();
        this.selectByPrimaryKeyQueryBuilder = new SelectByPrimaryKeyQueryBuilder(entityMetadata);
        this.selectQueryBuilder = new SelectQueryBuilder(entityMetadata);
        this.rowMapper = RowMapperFactory.create(entityClass);
    }

    public List<Object> load(Collection<Long> ids) {
        String query = selectQueryBuilder.buildQuery(Map.of("id", ids));
        return jdbcTemplate.query(query, rowMapper);
    }

    public Object load(Long id) {
        String query = selectByPrimaryKeyQueryBuilder.buildQuery(id);
        List<Object> result = jdbcTemplate.query(query, rowMapper);

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    // TODO: 데이터베이스에서 지원하는 방식으로 변경하기
    public Long getLastId() {
        String query = "SELECT max(id) as id FROM " + tableName;
        return jdbcTemplate.queryForObject(query, resultSet -> resultSet.getLong("id"));
    }
}
