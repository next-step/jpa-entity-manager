package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityLoader;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.sql.ddl.converter.H2TypeConverter;
import persistence.sql.ddl.mapping.DDLQueryBuilder;
import persistence.sql.ddl.mapping.H2PrimaryKeyGenerationType;
import persistence.sql.ddl.mapping.QueryBuilder;
import persistence.sql.ddl.model.DDLColumn;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.model.Table;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityPersisterTest {

    private EntityLoader entityLoader;
    private EntityPersister entityPersister;
    private EntityManager entityManager;
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private QueryBuilder queryBuilder;
    private Person expected;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        expected = DummyPerson.of();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        queryBuilder = new DDLQueryBuilder(
                new Table(expected.getClass()),
                new DDLColumn(new H2TypeConverter(), new H2PrimaryKeyGenerationType())
        );
        entityLoader = new EntityLoader(jdbcTemplate);
        entityPersister = new EntityPersister(jdbcTemplate);
        entityManager = new EntityManagerImpl(entityLoader, entityPersister);

        final String createQuery = queryBuilder.create(Person.class);
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown() {
        final String dropQuery = queryBuilder.drop(Person.class);
        jdbcTemplate.execute(dropQuery);

        server.stop();
    }

    @Test
    @DisplayName("EntityPersister 를 통해 Object 를 Insert 한다.")
    void entityPersisterInsertTest() {
        entityPersister.insert(expected);

        final var actual = entityManager.find(Person.class, 1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("EntityPersister 를 통해 id 에 해당하는 Object 를 Delete 한다.")
    void entityPersisterDeleteTest() {
        insertDummyPerson();

        entityPersister.delete(expected);

        assertThrows(
                RuntimeException.class,
                () -> entityManager.find(Person.class, 1L)
        );
    }

    @Test
    @DisplayName("EntityPersister 를 통해 id 에 해당하는 Object 를 Update 한다.")
    void test() {
        insertDummyPerson();
        expected = new Person("updateName", 30, "b@b.com");

        final var actual = entityPersister.update(expected, 1L);

        assertThat(actual).isEqualTo(expected);
    }

    private void insertDummyPerson() {
        final var queryBuilder = new InsertQueryBuilder(expected);
        final var insertQuery = queryBuilder.build();

        jdbcTemplate.execute(insertQuery);
    }

}
