package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.ddl.EntityMetadata;
import persistence.sql.dml.EntityManipulationBuilder;

import java.sql.ResultSet;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T findById(Class<T> clazz, Long id) {
        EntityMetadata entityMetadata = EntityMetadata.of(clazz);
        return jdbcTemplate.queryForObject(
                new EntityManipulationBuilder().findById(id, entityMetadata),
                resultSet -> {
                    if (!resultSet.next()) {
                        return null;
                    }

                    return getEntity(resultSet, entityMetadata);
                });
    }

    private <T> T getEntity(ResultSet resultSet, EntityMetadata entityMetadata) {
        return entityMetadata.getEntity(resultSet);
    }

}
