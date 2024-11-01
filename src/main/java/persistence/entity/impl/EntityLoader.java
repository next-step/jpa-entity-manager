package persistence.entity.impl;


import jdbc.JdbcTemplate;
import persistence.entity.EntityRowMapper;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;

/**
 * EntityLoader - 주요 역활
 * 지연 로딩 -> 엔티티의 데이터를 DB에서 즉시 로드하는 대신 실제 필요할때 로딩.
 * 배치 로딩 -> N+1 문제 해결
 * 캐시 활용 -> DB 부하 최소화
 * 특정 조회 전략 -> DB 부하 최소화
 * -----------------------------------------------------
 * 일반적 기능
 * ID 기반 엔티티 로드
 * 조건 기반 엔티티 검색
 * 관계된 엔티티들의 로드
 * 캐시 관리
 */
public class EntityLoader<T> {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public T load(Class<T> clazz, Long id) {
        try {
            SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
            return jdbcTemplate.queryForObject(selectQueryBuilder.findById(clazz, id), new EntityRowMapper<>(clazz));
        } catch (RuntimeException e) {
            return null;
        }
    }

    public List<T> loadAll(Class<T> clazz) {
        try {
            SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
            return jdbcTemplate.query(selectQueryBuilder.findAll(clazz), new EntityRowMapper<>(clazz));
        } catch (RuntimeException e) {
            return null;
        }
    }
}
