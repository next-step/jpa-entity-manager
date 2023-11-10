package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.ddl.EntityMetadata;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.dml.EntityManipulationBuilder;

import java.sql.ResultSet;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    private final Dialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> T findById(Class<T> clazz, Long id) {
        EntityMetadata entityMetadata = EntityMetadata.of(clazz, dialect);
        return jdbcTemplate.queryForObject(EntityManipulationBuilder.findById(id, entityMetadata),
                resultSet -> {
                    if (!resultSet.next()) {
                        return null;
                    }

                    return getEntity(resultSet, entityMetadata);
                });
    }

    public <T> T getEntity(ResultSet resultSet, EntityMetadata entityMetadata) {
        return entityMetadata.getEntity(resultSet);
    }

}
