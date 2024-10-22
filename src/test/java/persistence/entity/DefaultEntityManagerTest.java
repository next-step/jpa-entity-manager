package persistence.entity;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.H2Dialect;
import persistence.fixture.EntityWithId;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityManagerTest {
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        entityPersister = new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(), new UpdateQueryBuilder(),
                new DeleteQueryBuilder());

        createTable();
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
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity, entityManager);

        // when
        final EntityWithId managedEntity = entityManager.find(entity.getClass(), entity.getId());

        // then
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isEqualTo(entity.getId()),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("엔티티를 저장한다.")
    void persist() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);

        // when
        entityManager.persist(entity);

        // then
        final EntityWithId managedEntity = entityManager.find(entity.getClass(), entity.getId());
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isNotNull(),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNotNull()
        );
    }

    @Test
    @DisplayName("엔티티를 삭제한다.")
    void remove() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity, entityManager);

        // when
        entityManager.remove(entity);

        // then
        assertThatThrownBy(() -> entityManager.find(entity.getClass(), entity.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got");
    }

    @Test
    @DisplayName("더티체킹으로 엔티티를 수정한다.")
    void update() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity, entityManager);
        entity.setName("Yang");
        entity.setAge(35);
        entity.setEmail("test2@email.com");

        // when
        entityManager.update(entity);

        // then
        final EntityWithId managedEntity = entityManager.find(entity.getClass(), entity.getId());
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isEqualTo(entity.getId()),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNotNull()
        );
    }

    private void createTable() {
        final CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(EntityWithId.class, new H2Dialect());
        jdbcTemplate.execute(createQueryBuilder.create());
    }

    private void insertData(EntityWithId entity, EntityManager entityManager) {
        entityManager.persist(entity);
    }

    private void dropTable() {
        final DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(EntityWithId.class);
        jdbcTemplate.execute(dropQueryBuilder.drop());
    }
}
