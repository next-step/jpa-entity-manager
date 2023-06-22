package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

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
}
