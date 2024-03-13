package persistence.context;

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
import persistence.entity.EntityLoader;
import persistence.entity.EntityLoaderImpl;
import persistence.entity.EntityPersister;
import persistence.entity.EntityPersisterImpl;
import persistence.entity.EntitySnapshot;
import persistence.entity.SimpleEntityManager;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import pojo.EntityMetaData;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplePersistenceContextTest {

    static Dialect dialect = new H2Dialect();
    static EntityMetaData entityMetaData = new EntityMetaData(Person3.class);

    static DatabaseServer server;
    static JdbcTemplate jdbcTemplate;
    static EntityPersister entityPersister;
    static EntityLoader entityLoader;
    static SimpleEntityManager simpleEntityManager;
    static PersistenceContext persistenceContext;

    Person3 person;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityPersister = new EntityPersisterImpl(jdbcTemplate, entityMetaData);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, entityMetaData);
        persistenceContext = new SimplePersistenceContext();
        simpleEntityManager = new SimpleEntityManager(dialect, entityPersister, entityLoader, persistenceContext);
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

    @DisplayName("persist 후 조회 시 cachedSnapshot 과 일치 여부 확인")
    @Test
    void addEntityAndGetCachedDatabaseSnapshotTest() {
        insertData();
        simpleEntityManager.find(person, person.getClass(), person.getId());

        EntitySnapshot cachedDatabaseSnapshot = persistenceContext.getDatabaseSnapshot(person.getId(), person);

        Field field = new FieldInfos(person.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, person);

        assertEquals(cachedDatabaseSnapshot.getMap().get(idField.getFieldNameData()), Long.toString(person.getId()));
    }

    private void createTable() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(dialect, entityMetaData);
        jdbcTemplate.execute(createQueryBuilder.createTable(person));
    }

    private void insertData() {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entityMetaData);
        jdbcTemplate.execute(updateQueryBuilder.insertQuery(person));
    }

    private void dropTable() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMetaData);
        jdbcTemplate.execute(dropQueryBuilder.dropTable());
    }
}
