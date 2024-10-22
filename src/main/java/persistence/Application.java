package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.EntityRowMapper;
import persistence.entity.PersistenceContextImpl;
import persistence.sql.H2Dialect;
import persistence.sql.Person;
import persistence.sql.ddl.query.CreateTableQueryBuilder;
import persistence.sql.ddl.query.DropQueryBuilder;
import persistence.sql.dml.query.SelectAllQueryBuilder;

import java.util.List;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            final Class<?> testClass = Person.class;
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            final EntityManager em = new EntityManagerImpl(jdbcTemplate, new PersistenceContextImpl());

            Person person1 = new Person("a", 10, "aaa@gmail.com", 1);
            Person person2 = new Person("b", 20, "bbb@gmail.com", 2);
            Person person3 = new Person("c", 30, "ccc@gmail.com", 3);

            // create table
            create(jdbcTemplate, testClass);

            // test insert and select
            insert(em, person1);
            insert(em, person1);
            insert(em, person2);
            insert(em, person3);
            try {
                insert(em, new Person(6L, "d", 1, "aaa", 1));
            } catch (Exception e) {
                logger.error("expect error", e);
            }
            selectAll(jdbcTemplate, testClass);
            select(em, 1L);
            select(em, 2L);
            select(em, 3L);
            logger.info("Remove person1");
            remove(em, person1);
            selectAll(jdbcTemplate, testClass);

            logger.info("Update person2");

            update(em, new Person(2L, "b", 25, "ddd@gmail.com", 5));
            selectAll(jdbcTemplate, testClass);
            drop(jdbcTemplate);

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    private static void create(JdbcTemplate jdbcTemplate, Class<?> testClass) {
        CreateTableQueryBuilder createQuery = new CreateTableQueryBuilder(new H2Dialect(), testClass);
        jdbcTemplate.execute(createQuery.build());
    }

    private static void drop(JdbcTemplate jdbcTemplate) {
        DropQueryBuilder dropQuery = new DropQueryBuilder(Person.class);
        String build = dropQuery.build();
        logger.info("Drop query: {}", build);
        jdbcTemplate.execute(build);
    }

    private static void selectAll(JdbcTemplate jdbcTemplate, Class<?> testClass) {
        String query = new SelectAllQueryBuilder(testClass).build();
        List<Person> people = jdbcTemplate.query(query, new EntityRowMapper<>(Person.class));

        for (Person person : people) {
            logger.info("Person: {}", person);
        }
    }

    private static void select(EntityManager em, Object id) {
        Person person = em.find(Person.class, id);
        logger.info("Person: {}", person);
    }

    private static void insert(EntityManager em, Person person) {
        em.persist(person);
        logger.info("Data inserted successfully!");
    }

    private static void update(EntityManager em, Person person) {
        em.update(person);
        logger.info("Data updated successfully!");
    }

    private static void remove(EntityManager em, Person person) {
        em.remove(person);
        logger.info("Data deleted successfully!");
    }
}
