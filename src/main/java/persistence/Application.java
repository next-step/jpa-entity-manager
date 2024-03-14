package persistence;

import database.DatabaseServer;
import database.H2;
import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityPersister;
import persistence.entity.EntityRowMapperFactory;
import persistence.entity.impl.EntityPersisterImpl;
import persistence.sql.QueryBuilder;
import persistence.sql.ddl.entity.Person;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            cretateTable(jdbcTemplate);

            EntityPersister entityPersister = new EntityPersisterImpl(jdbcTemplate);

            getPersons().forEach(entityPersister::insert);

            getUpdatedPersons().forEach(entityPersister::update);

            querySelectAll(jdbcTemplate);

            Person deletedPerson = getUpdatedPersons().get(0);

            entityPersister.delete(deletedPerson);

            querySelectAll(jdbcTemplate);

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    private static void cretateTable(JdbcTemplate jdbcTemplate) {
        QueryBuilder queryBuilder = new QueryBuilder();

        jdbcTemplate.execute(queryBuilder.getCreateTableQuery(Person.class));
    }

    private static void querySelectById(JdbcTemplate jdbcTemplate) {
        QueryBuilder queryBuilder = new QueryBuilder();

        Person person = jdbcTemplate.queryForObject(
            queryBuilder.getSelectByIdQuery(Person.class, 2L),
            EntityRowMapperFactory.getInstance().getRowMapper(Person.class)
        );

        logger.info("Person: {}", person);
    }

    private static void querySelectAll(JdbcTemplate jdbcTemplate) {
        QueryBuilder queryBuilder = new QueryBuilder();

        List<Person> persons = jdbcTemplate.query(
            queryBuilder.getSelectAllQuery(Person.class),
            EntityRowMapperFactory.getInstance().getRowMapper(Person.class)
        );

        persons.forEach(person -> logger.info("Person: {}", person));
    }

    private static void executeInitializedQuery(JdbcTemplate jdbcTemplate, QueryBuilder queryBuilder) {
        List<Person> persons = getPersons();

        persons.stream()
            .map(queryBuilder::getInsertQuery)
            .forEach(jdbcTemplate::execute);
    }

    private static List<Person> getPersons() {
        return List.of(
            new Person("John", 23, "john@gmail.com"),
            new Person("Smith", 33, "smith@gmail.com"),
            new Person("rolroralra", 37, "rolroralra@gmail.com")
        );
    }

    private static List<Person> getUpdatedPersons() {
        return List.of(
            new Person(1L, "John2", 25, "john@gmail.com"),
            new Person(2L, "Smith2", 33, "smith2@gmail.com"),
            new Person(3L, "rolroralra2", 40, "rolroralra2@gmail.com")
        );
    }
}
