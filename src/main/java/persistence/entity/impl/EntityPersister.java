package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.entity.EntityRowMapper;

import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.util.List;

/**
 * EntityPersister 주요 역활
 * Entity와 테이블간의 배핑정보 관리
 * sql문 생성
 * Entity의 메타 데이터 관리
 * Entity의 생명주기 이벤트처리
 */

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object find(Class<?> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        String selectQuery = selectQueryBuilder.findById(clazz, id);
        List<?> query = jdbcTemplate.query(selectQuery, new EntityRowMapper<>(clazz));
        if (query.isEmpty()) {
            return null;
        }
        return query.getFirst();
    }

    public void update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entity.getClass());
        String updateQuery = updateQueryBuilder.update(entity);
        jdbcTemplate.execute(updateQuery);
    }

    public void remove(Class<?> clazz, Long id) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(clazz);
        String deleteQuery = deleteQueryBuilder.deleteById(clazz, id);
        jdbcTemplate.execute(deleteQuery);
    }

    public Long insert(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entity.getClass());
        String insertQuery = insertQueryBuilder.insert(entity);
        return jdbcTemplate.executeInsert(insertQuery);

    }
}
