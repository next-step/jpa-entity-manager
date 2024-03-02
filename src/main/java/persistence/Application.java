package persistence;

import database.DatabaseServer;
import database.H2;
import database.dialect.MySQLDialect;
import database.sql.Person;
import database.sql.ddl.QueryBuilder;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManagerImpl;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            String query = QueryBuilder.getInstance().buildCreateQuery(Person.class, MySQLDialect.INSTANCE);
            jdbcTemplate.execute(query);

            EntityManagerImpl entityManager = EntityManagerImpl.from(jdbcTemplate);
            entityManager.persist(new Person("abc", 18, "abc@example.com"));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            entityManager.persist(new Person(1L, "abc123", 18, "abc123@example.com"));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            entityManager.persist(new Person(1L, "abc123", 18, "abc123@example.com"));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));
            System.out.println(entityManager.find(Person.class, 1L));

            entityManager.persist(new Person("abc567", 118, "abc567@example.com"));
            System.out.println(entityManager.find(Person.class, 2L));
            System.out.println(entityManager.find(Person.class, 2L));
            System.out.println(entityManager.find(Person.class, 2L));
            entityManager.persist(new Person(2L, "abc890", 1118, "abc890@example.com"));
            System.out.println(entityManager.find(Person.class, 2L));
            System.out.println(entityManager.find(Person.class, 2L));
            System.out.println(entityManager.find(Person.class, 2L));
            entityManager.persist(new Person(2L, "abc8901", 1118, "abc890@example.com"));
            System.out.println(entityManager.find(Person.class, 2L));
            System.out.println(entityManager.find(Person.class, 2L));
            System.out.println(entityManager.find(Person.class, 2L));

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
