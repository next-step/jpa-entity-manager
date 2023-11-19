package persistence.entitymanager;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;

import java.util.List;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void insert(Object entity){
        String query = InsertQueryBuilder.getQuery(entity);
        jdbcTemplate.execute(query);
    }

    public <T> void delete(Object entity){
        DeleteQueryBuilder.getQuery(entity);

    }
}
