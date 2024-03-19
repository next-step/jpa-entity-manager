package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.context.SimplePersistenceContext;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityPersisterTest {
    private final Dialect DIALECT = new H2Dialect();
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;
    private EntityPersister entityPersister;
    private DatabaseServer databaseServer;
    private SimplePersistenceContext persistenceContext;

    @BeforeEach
    void setUp() throws SQLException {
        CreateQueryBuilder createQueryBuilder = CreateQueryBuilder.builder()
                .dialect(DIALECT)
                .entity(Person.class)
                .build();

        databaseServer = new H2();
        databaseServer.start();

        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        persistenceContext = new SimplePersistenceContext();
        entityManager = new SimpleEntityManager(jdbcTemplate, DIALECT, persistenceContext);
        entityPersister = new EntityPersister(jdbcTemplate, DIALECT);

        jdbcTemplate.execute(createQueryBuilder.generateQuery());
    }

    @AfterEach
    void tearDown() {
        DropQueryBuilder dropQueryBuilder = DropQueryBuilder.builder()
                .dialect(DIALECT)
                .entity(Person.class)
                .build();

        jdbcTemplate.execute(dropQueryBuilder.generateQuery());
        databaseServer.stop();
    }

    @Test
    @DisplayName("Update query builder 동작 테스트")
    void update() {
        // given
        final String name = "joel";
        final Integer age = 30;
        final Person person = Person.of(1L, "crong", 35, "test@gmail.com");
        entityPersister.insert(person);

        // when
        Person findPerson = Person.of(1L, name, age, "test@gmail.com");
        entityPersister.update(findPerson);

        // then
        assertThat(entityManager.find(Person.class, 1L)).isEqualTo(findPerson);
    }

    @Test
    @DisplayName("Insert query builder 동작 테스트")
    void insert() {
        // given
        final String name = "crong";
        final Integer age = 35;
        final String email = "test@gmail.com";
        final Person person = Person.of(1L, name, age, email);

        // when
        entityPersister.insert(person);

        // then
        assertThat(entityManager.find(Person.class, 1L)).isEqualTo(person);
    }

    @Test
    @DisplayName("Delete query builder 동작 테스트")
    void delete() {
        // given
        final Person person = Person.of("crong", 35, "test@gmail.com");
        entityManager.persist(person);

        // when
        entityManager.remove(person);

        // then
        assertThatThrownBy(() -> entityManager.find(Person.class, 1L)).isInstanceOf(RuntimeException.class);
    }
}