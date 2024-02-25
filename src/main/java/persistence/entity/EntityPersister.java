package persistence.entity;

import java.util.Objects;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Columns;

public class EntityPersister {

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;

    private EntityPersister(JdbcTemplate jdbcTemplate) {
        this.dmlGenerator = DmlGenerator.from();
        this.jdbcTemplate = jdbcTemplate;
    }

    public static EntityPersister from(JdbcTemplate jdbcTemplate) {
        return new EntityPersister(jdbcTemplate);
    }

    public boolean update(Object entity) {
        return jdbcTemplate.executeUpdate(dmlGenerator.generateUpdateQuery(entity)) > 0;
    }

    public void insert(Object entity) {
        jdbcTemplate.execute(dmlGenerator.generateInsertQuery(entity));
    }

    public void delete(Object entity) {
        Columns columns = Columns.from(entity.getClass().getDeclaredFields());
        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(entity.getClass(),
            columns.getIdValue(entity)));
    }

    public boolean isExist(Object entity) {
        Columns columns = Columns.from(entity.getClass().getDeclaredFields());
        return Objects.nonNull(columns.getIdValue(entity));
    }
}
