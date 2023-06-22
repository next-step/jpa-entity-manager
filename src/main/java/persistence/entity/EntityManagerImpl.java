package persistence.entity;

import jakarta.persistence.Id;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.ddl.exception.NoIdentifierException;
import persistence.sql.dml.DmlGenerator;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        DmlGenerator generator = new DmlGenerator(clazz);
        String query = generator.generateFindByIdQuery(id);
        return jdbcTemplate.queryForObject(query, new EntityRowMapper<>(clazz));
    }

    @Override
    public void persist(Object entity) {
        DmlGenerator generator = new DmlGenerator(entity.getClass());
        String query = generator.generateInsertQuery(entity);
        jdbcTemplate.execute(query);
    }

    @Override
    public void remove(Object entity) {
        Long id = findPrimaryKey(entity);
        DmlGenerator generator = new DmlGenerator(entity.getClass());
        String query = generator.generateDeleteByIdQuery(id);
        jdbcTemplate.execute(query);
    }

    private Long findPrimaryKey(Object entity) {
        try {
            Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new NoIdentifierException(entity.getClass().getSimpleName()));
            idField.setAccessible(true);
            return (Long) idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
