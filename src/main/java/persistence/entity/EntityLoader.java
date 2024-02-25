package persistence.entity;

import database.sql.dml.SelectOneQueryBuilder;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final SelectOneQueryBuilder selectOneQueryBuilder;
    private final RowMapper<Object> rowMapper;

    public EntityLoader(JdbcTemplate jdbcTemplate, Class<?> entityClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectOneQueryBuilder = new SelectOneQueryBuilder(entityClass);
        this.rowMapper = RowMapperFactory.create(entityClass);
    }

    // XXX: SELECT 쿼리 빌더로 변경하기
    public List<Object> load(Collection<Long> ids) {
        return ids.stream()
                .map(id -> {
                         String query = selectOneQueryBuilder.buildQuery(id);
                         jdbcTemplate.queryForObject(query, rowMapper);
                         return jdbcTemplate.queryForObject(query, rowMapper);
                     }
                )
                .collect(Collectors.toList());
    }

    public Object load(Long id) {
        String query = selectOneQueryBuilder.buildQuery(id);
        return jdbcTemplate.queryForObject(query, rowMapper);
    }
}
