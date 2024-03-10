package persistence.entity;

import pojo.EntityMetaData;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaData entityMetaData;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, EntityMetaData entityMetaData) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaData = entityMetaData;
    }

    public <T> T findById(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityMetaData);
        return jdbcTemplate.queryForObject(selectQueryBuilder.findByIdQuery(clazz, id), new RowMapperImpl<>(clazz));
    }

    public <T> List<T> findAll(Class<T> clazz) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityMetaData);
        return jdbcTemplate.query(selectQueryBuilder.findAllQuery(), new RowMapperImpl<>(clazz));
    }
}
