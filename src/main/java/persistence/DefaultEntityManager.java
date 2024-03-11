package persistence;

import domain.Person;
import jakarta.persistence.Entity;
import jdbc.JdbcTemplate;
import jdbc.PersonRowMapper;
import persistence.sql.dml.DMLGenerator;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final DMLGenerator dmlGenerator;
    private final EntityPersister entityPersister;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate, DMLGenerator dmlGenerator, EntityPersister entityPersister) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
        this.entityPersister = entityPersister;
    }

    @Override
    public Person find(Class<Person> clazz, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("[EntityManager] find: id is null");
        }

        String sql = dmlGenerator.generateFindById(id);
        return jdbcTemplate.queryForObject(sql, new PersonRowMapper());
    }

    @Override
    public void persist(Object entity) {
        validEntity(entity);

        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        validEntity(entity);

        entityPersister.delete(entity);
    }

    private static void validEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("[EntityManager] persist: the instance is not an entity");
        }
    }
}
