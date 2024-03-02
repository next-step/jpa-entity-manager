package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.MyEntityManager;
import persistence.sql.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.domain.dialect.H2Dialect;
import repository.CustomJpaRepository;

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

            Person person = new Person(null, "John", 25, "email", 1);
            Person person2 = new Person(1L, "James", 45, "james@asdf.com", 10);
            MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
            CustomJpaRepository<Person, Long> personLongCustomJpaRepository = new CustomJpaRepository<Person, Long>(entityManager);
            personLongCustomJpaRepository.save(person);
            personLongCustomJpaRepository.save(person2);

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
