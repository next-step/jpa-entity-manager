package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import domain.PersonFixture;
import domain.PersonNotFoundException;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.CustomJpaRepository;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.StatefulPersistenceContext;
import persistence.sql.ddl.DdlBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.DmlBuilder;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            final Dialect dialect = Dialect.H2;
            final DdlBuilder ddl = dialect.getDdl();
            final DmlBuilder dml = dialect.getDml();
            final EntityManager em = new EntityManagerImpl(
                    new StatefulPersistenceContext(),
                    jdbcTemplate, dml
            );
            final CustomJpaRepository<Person, Long> jpa = new CustomJpaRepository<>(em);

            jdbcTemplate.execute(
                    ddl.getCreateQuery(Person.class)
            );

            Person person = PersonFixture.createPerson();
            jpa.save(person);
            person.setName("Changed Name");
            jpa.save(person);


            Person entity = em.find(Person.class, 1L)
                    .orElseThrow(() -> new PersonNotFoundException());
            em.remove(entity);

            jdbcTemplate.execute(
                    ddl.getDropQuery(Person.class)
            );
            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
