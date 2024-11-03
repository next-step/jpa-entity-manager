package persistence.entity.impl;

import domain.Person;
import jdbc.JdbcTemplate;
import persistence.entity.EntityRowMapper;

import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * EntityPersister 주요 역활
 * Entity와 테이블간의 배핑정보 관리
 * sql문 생성
 * Entity의 메타 데이터 관리
 * Entity의 생명주기 이벤트처리
 */

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final EntityLoader entityLoader;


    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoader = new EntityLoader<>(jdbcTemplate);
    }

    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return Optional.ofNullable(clazz.cast(entityLoader.load(clazz, id)));
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
