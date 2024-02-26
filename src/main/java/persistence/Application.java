package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityPersister;
import persistence.entity.MyEntityPersister;
import persistence.sql.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.domain.dialect.H2Dialect;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect());
            jdbcTemplate.execute(createQueryBuilder.build(Person.class));

            Person person = new Person(1L, "John", 25, "email", 1);
            Person person2 = new Person(2L, "James", 45, "james@asdf.com", 10);
            EntityPersister entityPersister = new MyEntityPersister(jdbcTemplate);
            entityPersister.insert(person);
            entityPersister.insert(person2);
            Person updatedPerson = new Person(2L, "Tom", 20, "james@asdf.com", 1);
            entityPersister.update(updatedPerson);
            entityPersister.delete(person);

            DropQueryBuilder dropQuery = new DropQueryBuilder(new H2Dialect());
            jdbcTemplate.execute(dropQuery.build(Person.class));

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
