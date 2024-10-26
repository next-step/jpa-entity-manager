package persistence.entity;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.H2Dialect;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithOnlyId;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityManagerTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());

        createTable();
    }

    @AfterEach
    void tearDown() {
        dropTable();
    }

    @Test
    @DisplayName("엔티티를 로드한다.")
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
                () -> assertThat(managedEntity.getIndex()).isNotNull()
        );
    }

    @Test
    @DisplayName("엔티티를 영속화한다.")
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
    @DisplayName("엔티티를 영속성 컨텍스트에 등록하고 flush() 한다.")
    void persistAndFlush() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithOnlyId entity = new EntityWithOnlyId(1L, "Jaden", 30, "test@email.com", 1);

        // when
        entityManager.persist(entity);
        entityManager.flush();

        // then
        final EntityWithOnlyId managedEntity = entityManager.find(entity.getClass(), entity.getId());
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
    @DisplayName("엔티티를 영속성 컨텍스트에 등록하고 flush() 한다.")
    void persistAnRemoveAndFlush() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithOnlyId entity = new EntityWithOnlyId(1L, "Jaden", 30, "test@email.com", 1);

        // when
        entityManager.persist(entity);
        entityManager.remove(entity);
        entityManager.flush();

        // then
        assertThatThrownBy(() -> entityManager.find(entity.getClass(), entity.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got");
    }

    @Test
    @DisplayName("영속화 불가능한 상태에서 엔티티를 영속화하면 예외를 발생한다.")
    void persist_exception() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity, entityManager);

        // when & then
        assertThatThrownBy(() -> entityManager.persist(entity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(DefaultEntityManager.NOT_PERSISTABLE_STATUS_FAILED_MESSAGE);

    }

    @Test
    @DisplayName("엔티티를 영속성 상태에서 제거한다.")
    void removeAndFlush() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity, entityManager);

        // when
        entityManager.remove(entity);
        entityManager.flush();

        // then
        assertThatThrownBy(() -> entityManager.find(entity.getClass(), entity.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got");
    }

    @Test
    @DisplayName("제거 불가능한 상태에서 엔티티를 제거하면 예외를 발생한다.")
    void remove_exception() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        entityManager.persist(entity);
        entityManager.remove(entity);
        entityManager.flush();

        // when & then
        assertThatThrownBy(() -> entityManager.remove(entity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(DefaultEntityManager.NOT_REMOVABLE_STATUS_FAILED_MESSAGE);

    }

    @Test
    @DisplayName("엔티티를 업데이트한다.")
    void updateAndFlush() {
        // given
        final EntityManager entityManager = DefaultEntityManager.of(jdbcTemplate);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity, entityManager);
        entity.setName("Yang");
        entity.setAge(35);
        entity.setEmail("test2@email.com");

        // when
        entityManager.flush();

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
