package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.example.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

//            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
//            final Dialect dialect = new H2Dialect();
//
//            final CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(Person.class, dialect);
//            jdbcTemplate.execute(createQueryBuilder.create());
//
//            final Person entity = new Person("Jaden", 30, "test@email.com", 1);
//            final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
//            jdbcTemplate.execute(insertQueryBuilder.insert(entity));
//
//            final Person updatedEntity = new Person(1L, "Jackson", 20, "test2@email.com");
//            final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
//            jdbcTemplate.execute(updateQueryBuilder.update(updatedEntity));
//
//            final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
//            final List<Person> people = jdbcTemplate.query(selectQueryBuilder.findAll(Person.class), new PersonRowMapper());
//            logger.debug(people.toString());
//
//            final Person person = jdbcTemplate.queryForObject(selectQueryBuilder.findById(Person.class, 1), new PersonRowMapper());
//            logger.debug(person.toString());
//
//            final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
//            jdbcTemplate.execute(deleteQueryBuilder.delete(updatedEntity));
//
//            final DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(Person.class);
//            jdbcTemplate.execute(dropQueryBuilder.drop());

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    private static class PersonRowMapper implements RowMapper<Person> {
        @Override
        public Person mapRow(ResultSet resultSet) throws SQLException {
            return new Person(
                    resultSet.getLong("id"),
                    resultSet.getString("nick_name"),
                    resultSet.getInt("old"),
                    resultSet.getString("email")
            );
        }
    }
}
