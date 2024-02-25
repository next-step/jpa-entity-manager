package persistence;

import database.DatabaseServer;
import jdbc.JdbcTemplate;
import jdbc.PersonRowMapper;
import persistence.entity.Person;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.ddl.DDLQueryBuilderFactory;
import persistence.sql.dml.DMLQueryBuilder;

import java.sql.SQLException;

public class EntityManagerImpl implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final DDLQueryBuilder ddlQueryBuilder;
    private DMLQueryBuilder dmlQueryBuilder;
    public EntityManagerImpl(DatabaseServer server) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(server.getConnection());
        this.ddlQueryBuilder = DDLQueryBuilderFactory.getDDLQueryBuilder(server);
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    @Override
    public <T> void createTable(Class<T> tClass) {
        jdbcTemplate.execute(ddlQueryBuilder.createTableQuery(tClass));
    }

    @Override
    public void dropTable(Class<Person> personClass) {
        jdbcTemplate.execute(ddlQueryBuilder.dropTableQuery(personClass));
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        return (T) jdbcTemplate.queryForObject(dmlQueryBuilder.selectByIdQuery(clazz, Id), new PersonRowMapper());
    }

    @Override
    public <T> T persist(T entity) {
        jdbcTemplate.execute(dmlQueryBuilder.insertSql(entity));
        return entity;
    }

    @Override
    public void remove(Object entity) {
        jdbcTemplate.execute(dmlQueryBuilder.deleteSql(entity));
    }
}
