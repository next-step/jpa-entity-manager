package persistence.study;

import database.Database;
import database.DatabaseServer;
import database.H2;
import database.SimpleDatabase;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import persistence.entity.*;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.model.Table;
import persistence.study.sql.ddl.Person3;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomJpaRepositoryTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private static DDLQueryBuilder ddlQueryBuilder;

    private static EntityPersister entityPersister;
    private static EntityLoader entityLoader;
    private static EntityManager entityManager;

    @BeforeAll
    static void initialize() throws SQLException {
        server = new H2();
        server.start();

        Connection jdbcConnection = server.getConnection();
        jdbcTemplate = new JdbcTemplate(jdbcConnection);
        Database database = new SimpleDatabase(jdbcTemplate);

        entityPersister = new EntityPersister(database);
        entityLoader = new EntityLoader(database);
        entityManager = new SimpleEntityManager(entityPersister, entityLoader);

        Dialect dialect = new H2Dialect();
        Table table = new Table(Person3.class);
        ddlQueryBuilder = new DDLQueryBuilder(table, dialect);
    }

    @AfterAll
    static void close() {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        String createTableQuery = ddlQueryBuilder.buildCreateQuery();
        jdbcTemplate.execute(createTableQuery);

        Person3 person = new Person3("qwer", 123, "email@email.com");
        entityManager.persist(person);
    }

    @AfterEach
    void setDown() {
        String dropQuery = ddlQueryBuilder.buildDropQuery();
        jdbcTemplate.execute(dropQuery);

        entityManager = new SimpleEntityManager(entityPersister, entityLoader);
    }

    @Test
    @DisplayName("merge 시 dirty check 한 후 변경사항이 있으면 업데이트된다.")
    void saveWithDirty() {
        Person3 changePerson = new Person3(1L, "dirty", 1, "check@email.com");

        entityManager.merge(changePerson);

        Person3 expect = entityManager.find(Person3.class, new EntityId(1L));
        assertThat(changePerson).isEqualTo(expect);
    }
}
