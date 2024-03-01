package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.Person;
import persistence.sql.dml.BooleanExpression;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;

public class EntityMangerImpl implements EntityManger {
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;

    public EntityMangerImpl(JdbcTemplate jdbcTemplate, EntityPersister entityPersister) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = entityPersister;
    }

    @Override
    public Person find(Class<Person> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        WhereBuilder builder = new WhereBuilder();
        builder.and(BooleanExpression.eq("id", id));
        String query = selectQueryBuilder.build(builder);

        return jdbcTemplate.queryForObject(query, getRowMapper());
    }

    public static RowMapper<Person> getRowMapper() {
        return rs ->
                new Person(
                        rs.getLong("id"),
                        rs.getString("nick_name"),
                        rs.getInt("old"),
                        rs.getString("email"),
                        null
                );
    }

    @Override
    public Object persist(Object entity) {
        boolean isEntityUpdated = entityPersister.update(entity);
        if(!isEntityUpdated){
            entityPersister.insert(entity);
        }

        return entity;
    }


    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
