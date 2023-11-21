package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import persistence.entity.*;
import persistence.sql.ddl.EntityDefinitionBuilder;
import persistence.sql.ddl.EntityMetadata;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.ddl.dialect.H2Dialect;
import persistence.sql.dml.EntityManipulationBuilder;

import java.sql.SQLException;

public abstract class DatabaseTestBase {

    protected static DatabaseServer server;
    protected static JdbcTemplate jdbcTemplate;
    protected EntityDefinitionBuilder entityDefinitionBuilder;
    protected EntityManager entityManager;
    protected EntityPersister entityPersister;
    protected EntityLoader entityLoader;

    protected PersistenceContext persistenceContext;

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @BeforeEach
    void beforeEach() {
        EntityMetadata entityMetadata = EntityMetadata.of(Person.class);
        entityDefinitionBuilder = new EntityDefinitionBuilder(entityMetadata);
        Dialect dialect = new H2Dialect();
        entityPersister = new EntityPersister(jdbcTemplate);
        entityLoader = new EntityLoader(jdbcTemplate);
        persistenceContext = new SimplePersistenceContext();
        entityManager = new SimpleEntityManager(entityPersister, entityLoader, persistenceContext);

        jdbcTemplate.execute(entityDefinitionBuilder.create(dialect));
        jdbcTemplate.execute(new EntityManipulationBuilder()
                .insert(Fixtures.person1(), entityMetadata)
        );

    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute(entityDefinitionBuilder.drop());
    }

}
