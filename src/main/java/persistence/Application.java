package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.SimpleEntityManager;
import persistence.loader.EntityLoader;
import persistence.persister.EntityPersister;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.TableCreateQueryBuilder;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");

        DatabaseServer server = null;
        try {
            server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            final H2Dialect h2Dialect = new H2Dialect();

            String sql = new TableCreateQueryBuilder(h2Dialect).generateSQLQuery(Person.class);
            logger.info(sql);
            jdbcTemplate.execute(sql);


            Person person = new Person(null, "aa", 10, "aa@aa.com");
            logger.info(String.valueOf(person));

            EntityPersister entityPersister = new EntityPersister(person.getClass(), jdbcTemplate);
            EntityLoader entityLoader = new EntityLoader(person.getClass(), jdbcTemplate);
            SimpleEntityManager entityManager = new SimpleEntityManager(entityLoader, entityPersister);
            entityManager.persist(person);
            logger.info(String.valueOf(person));

            person.setAge(20);
            entityManager.update(person);
            logger.info(String.valueOf(person));

            Person person1 = entityManager.find(person.getClass(), 1L);
            logger.info(String.valueOf(person1));

            entityManager.remove(person);
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
            if (server != null) {
                server.stop();
            }
        }
    }
}
