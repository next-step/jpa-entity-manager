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
import persistence.sql.dml.UpdateQueryBuilder;
import pojo.EntityMetaData;
import pojo.EntityStatus;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityPersisterImplTest {

    static Dialect dialect = new H2Dialect();
    static EntityMetaData entityMetaData = new EntityMetaData(Person3.class);

    static DatabaseServer server;
    static JdbcTemplate jdbcTemplate;
    static EntityPersister entityPersister;
    static EntityLoader entityLoader;
    static SimpleEntityManager simpleEntityManager;
    static PersistenceContext persistenceContext;
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

    @DisplayName("insert 테스트")
    @Test
    void insertTest() {
        entityPersister.insert(person);
        Person3 person3 = simpleEntityManager.find(person, person.getClass(), person.getId());
        assertAll(
                () -> assertThat(person3.getId()).isEqualTo(person.getId()),
                () -> assertThat(person3.getName()).isEqualTo(person.getName()),
                () -> assertThat(person3.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(person3.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @DisplayName("insert 후 update 테스트")
    @Test
    void updateTest() {
        insertData();
        boolean result = entityPersister.update(new Person3(person.getId(), "test", 35, "test@test.com"));
        assertThat(result).isTrue();
    }

    @DisplayName("delete 후 조회하려고 할 때 exception 테스트")
    @Test
    void deleteTest() {
        insertData();
        entityPersister.delete(person);
        assertThrows(RuntimeException.class, () -> entityLoader.findById(person.getClass(), person, person.getId()));
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
