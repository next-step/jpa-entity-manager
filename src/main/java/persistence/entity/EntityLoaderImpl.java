package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.SelectQueryBuilder;
import pojo.EntityMetaData;

import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaData entityMetaData;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, EntityMetaData entityMetaData) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaData = entityMetaData;
    }

    @Override
    public <T> T findById(Class<T> clazz, Object entity, Object condition) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityMetaData);
        return jdbcTemplate.queryForObject(selectQueryBuilder.findByIdQuery(entity, clazz, condition), new RowMapperImpl<>(clazz));
    }

    public <T> List<T> findAll(Class<T> clazz) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityMetaData);
        return jdbcTemplate.query(selectQueryBuilder.findAllQuery(), new RowMapperImpl<>(clazz));
    }
}
