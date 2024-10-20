package persistence.entity;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.example.Person;
import persistence.fixture.EntityWithId;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityManagerTest {
    private JdbcTemplate jdbcTemplate;
    private Dialect dialect;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        dialect = new H2Dialect();

        createTable();
        insertData();
    }

    @AfterEach
    void tearDown() {
        dropTable();
    }

    @Test
    @DisplayName("엔티티를 조회한다.")
    void find() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);

        // when
        final EntityWithId entityWithId = entityManager.find(EntityWithId.class, 1L);

        // then
        assertAll(
                () -> assertThat(entityWithId).isNotNull(),
                () -> assertThat(entityWithId.getId()).isEqualTo(1),
                () -> assertThat(entityWithId.getName()).isEqualTo("Jaden"),
                () -> assertThat(entityWithId.getAge()).isEqualTo(30),
                () -> assertThat(entityWithId.getEmail()).isEqualTo("test@email.com"),
                () -> assertThat(entityWithId.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("엔티티를 저장한다.")
    void persist() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entityWithId = new EntityWithId("Jaden", 30, "test@email.com", 1);

        // when
        entityManager.persist(entityWithId);

        // then
        final EntityWithId savedEntity = entityManager.find(EntityWithId.class, 1L);
        assertAll(
                () -> assertThat(savedEntity).isNotNull(),
                () -> assertThat(savedEntity.getId()).isNotNull(),
                () -> assertThat(savedEntity.getName()).isEqualTo(entityWithId.getName()),
                () -> assertThat(savedEntity.getAge()).isEqualTo(entityWithId.getAge()),
                () -> assertThat(savedEntity.getEmail()).isEqualTo(entityWithId.getEmail()),
                () -> assertThat(savedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("엔티티를 수정한다.")
    void update() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entityWithId = new EntityWithId(1L, "Jackson", 20, "test2@email.com");

        // when
        entityManager.update(entityWithId);

        // then
        final EntityWithId savedEntity = entityManager.find(EntityWithId.class, 1L);
        assertAll(
                () -> assertThat(savedEntity).isNotNull(),
                () -> assertThat(savedEntity.getId()).isEqualTo(entityWithId.getId()),
                () -> assertThat(savedEntity.getName()).isEqualTo(entityWithId.getName()),
                () -> assertThat(savedEntity.getAge()).isEqualTo(entityWithId.getAge()),
                () -> assertThat(savedEntity.getEmail()).isEqualTo(entityWithId.getEmail()),
                () -> assertThat(savedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("엔티티를 삭제한다.")
    void remove() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entityWithId = new EntityWithId(1L, "Jaden", 30, "test@email.com");

        // when
        entityManager.remove(entityWithId);

        // then
        assertThatThrownBy(() -> entityManager.find(EntityWithId.class, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got");
    }

    private void createTable() {
        final CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(Person.class, dialect);
        jdbcTemplate.execute(createQueryBuilder.create());
    }

    private void insertData() {
        final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(
                new EntityWithId("Jaden", 30, "test@email.com", 1)
        );
        jdbcTemplate.execute(insertQueryBuilder.insert());
    }

    private void dropTable() {
        final DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(Person.class);
        jdbcTemplate.execute(dropQueryBuilder.drop());
    }
}
