package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.ddl.CreateDdlBuilder;
import persistence.sql.ddl.InsertQueryBuilder;
import persistence.sql.ddl.h2.H2CreateDdlBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            CreateDdlBuilder createDdlBuilder = new H2CreateDdlBuilder();
            jdbcTemplate.execute(createDdlBuilder.createTableBuild(Person.class));

            Person person = new Person("slow", 20, "email@email.com", 1);
            InsertQueryBuilder insertQueryBuilder = new H2InsertQueryBuilder();
            jdbcTemplate.execute(insertQueryBuilder.createInsertBuild(person));

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
