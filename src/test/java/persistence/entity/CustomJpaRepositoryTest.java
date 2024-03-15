package persistence.entity;

import database.DatabaseServer;
import database.H2;
import dialect.Dialect;
import dialect.H2Dialect;
import entity.Person3;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import pojo.EntityMetaData;
import pojo.EntityStatus;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomJpaRepositoryTest {

    static Dialect dialect = new H2Dialect();
    static EntityMetaData entityMetaData = new EntityMetaData(Person3.class);

    static DatabaseServer server;
    static JdbcTemplate jdbcTemplate;
    static EntityPersister entityPersister;
    static EntityLoader entityLoader;
    static SimpleEntityManager simpleEntityManager;
    static PersistenceContext persistenceContext;
    static JpaRepository jpaRepository;
    static EntityEntry entityEntry;

    Person3 person;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityPersister = new EntityPersisterImpl(jdbcTemplate, entityMetaData);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, entityMetaData);
        persistenceContext = new SimplePersistenceContext();
        entityEntry = new SimpleEntityEntry(EntityStatus.LOADING);
        simpleEntityManager = new SimpleEntityManager(entityPersister, entityLoader, persistenceContext, entityEntry);
        jpaRepository = new CustomJpaRepository(simpleEntityManager);
    }

    @BeforeEach
    void setUp() {
        person = new Person3(1L, "test", 20, "test@test.com");
        createTable();
    }

    @AfterEach
    void remove() {
        dropTable();
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }


    @DisplayName("save 시 dirty checking 로직 구현")
    @Test
    void saveWithDirtyTest() {
        jpaRepository.save(person);

        Person3 updatedPerson = new Person3(person.getId(), "test2", 30, "test2@test.com");
        jpaRepository.save(updatedPerson);

        EntitySnapshot cachedDatabaseSnapshot = persistenceContext.getDatabaseSnapshot(person.getId(), person);
        Map<String, Object> map = cachedDatabaseSnapshot.getMap();

        assertEquals(Long.valueOf(String.valueOf(map.get("id"))), updatedPerson.getId());

        simpleEntityManager.remove(person);
    }

    private void createTable() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(dialect, entityMetaData);
        jdbcTemplate.execute(createQueryBuilder.createTable(person));
    }

    private void dropTable() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMetaData);
        jdbcTemplate.execute(dropQueryBuilder.dropTable());
    }
}
