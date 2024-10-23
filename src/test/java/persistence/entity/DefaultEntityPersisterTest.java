package persistence.entity;

import database.H2ConnectionFactory;
import jdbc.InstanceFactory;
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
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityPersisterTest {
    private JdbcTemplate jdbcTemplate;
    private EntityLoader entityLoader;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        entityLoader = new DefaultEntityLoader(jdbcTemplate, new SelectQueryBuilder());
        entityManager = DefaultEntityManager.of(jdbcTemplate);

        createTable();
    }

    @AfterEach
    void tearDown() {
        dropTable();
    }

    @Test
    @DisplayName("엔티티를 저장한다.")
    void insert() {
        // given
        final EntityPersister entityPersister = new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(),
                new UpdateQueryBuilder(), new DeleteQueryBuilder());
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);

        // when
        entityPersister.insert(entity);

        // then
        final EntityWithId managedEntity = entityLoader.load(entity.getClass(), entity.getId());
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isNotNull(),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("엔티티를 수정한다.")
    void update() {
        // given
        final EntityPersister entityPersister = new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(),
                new UpdateQueryBuilder(), new DeleteQueryBuilder());
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity);
        final EntityWithId updatedEntity = new EntityWithId(entity.getId(), "Jackson", 20, "test2@email.com");
        final Object snapshot = new InstanceFactory<>(entity.getClass()).copy(entity);

        // when
        entityPersister.update(updatedEntity, snapshot);

        // then
        final EntityWithId managedEntity = entityLoader.load(entity.getClass(), entity.getId());
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isEqualTo(updatedEntity.getId()),
                () -> assertThat(managedEntity.getName()).isEqualTo(updatedEntity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(updatedEntity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(updatedEntity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("변경된 필드가 없는 엔티티로 엔티티를 수정하면 예외를 발생한다.")
    void update_exception() {
        // given
        final EntityPersister entityPersister = new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(),
                new UpdateQueryBuilder(), new DeleteQueryBuilder());
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity);
        final Object snapshot = new InstanceFactory<>(entity.getClass()).copy(entity);

        // when & then
        assertThatThrownBy(() -> entityPersister.update(entity, snapshot))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(DefaultEntityPersister.NOT_CHANGED_MESSAGE);
    }

    @Test
    @DisplayName("엔티티를 삭제한다.")
    void delete() {
        // given
        final EntityPersister entityPersister = new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(),
                new UpdateQueryBuilder(), new DeleteQueryBuilder());
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity);

        // when
        entityPersister.delete(entity);

        // then
        assertThatThrownBy(() -> entityLoader.load(entity.getClass(), entity.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got");
    }

    private void createTable() {
        final CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(EntityWithId.class, new H2Dialect());
        jdbcTemplate.execute(createQueryBuilder.create());
    }

    private void insertData(EntityWithId entity) {
        entityManager.persist(entity);
    }

    private void dropTable() {
        final DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(EntityWithId.class);
        jdbcTemplate.execute(dropQueryBuilder.drop());
    }
}
