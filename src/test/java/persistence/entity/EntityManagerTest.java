package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.query.CreateTableQueryBuilder;
import persistence.sql.ddl.query.DropQueryBuilder;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EntityManagerTest {

    @Entity
    private static class EntityManagerTestEntityWithAutoId {
        @Id
        private Long id;

        private String name;

        private Integer age;

        public EntityManagerTestEntityWithAutoId() {
        }

        public EntityManagerTestEntityWithAutoId(Long id, Integer age) {
            this.id = id;
            this.age = age;
        }

        public EntityManagerTestEntityWithAutoId(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    @Entity
    private static class EntityManagerTestEntityWithIdentityId {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private Integer age;

        public EntityManagerTestEntityWithIdentityId() {
        }

        public EntityManagerTestEntityWithIdentityId(Long id, Integer age) {
            this.id = id;
            this.age = age;
        }

        public EntityManagerTestEntityWithIdentityId(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    private static DatabaseServer server;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        String query1 = new CreateTableQueryBuilder(new H2Dialect(), EntityManagerTestEntityWithAutoId.class).build();
        String query2 = new CreateTableQueryBuilder(new H2Dialect(), EntityManagerTestEntityWithIdentityId.class).build();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(query1);
        jdbcTemplate.execute(query2);
    }

    @AfterEach
    void tearDown() throws SQLException {
        String query1 = new DropQueryBuilder(EntityManagerTestEntityWithAutoId.class).build();
        String query2 = new DropQueryBuilder(EntityManagerTestEntityWithIdentityId.class).build();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(query1);
        jdbcTemplate.execute(query2);
        server.stop();
    }

    @Test
    @DisplayName("Auto 전략을 사용하는 엔티티를 저장한다.")
    void testPersist() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithAutoId entity = new EntityManagerTestEntityWithAutoId(1L, "john_doe", 30);
        entityManager.persist(entity);

        EntityManagerTestEntityWithAutoId persistedEntity = entityManager.find(EntityManagerTestEntityWithAutoId.class, 1L);
        assertAll(
                () -> assertThat(persistedEntity.id).isEqualTo(1L),
                () -> assertThat(persistedEntity.name).isEqualTo("john_doe"),
                () -> assertThat(persistedEntity.age).isEqualTo(30)
        );
    }

    @Test
    @DisplayName("Identity 전략을 사용하는 엔티티를 저장한다.")
    void testPersistWithIdentityId() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithIdentityId entity = new EntityManagerTestEntityWithIdentityId(null, "john_doe", 30);
        entityManager.persist(entity);

        EntityManagerTestEntityWithIdentityId persistedEntity = entityManager.find(EntityManagerTestEntityWithIdentityId.class, 1L);
        assertAll(
                () -> assertThat(persistedEntity.id).isEqualTo(1L),
                () -> assertThat(persistedEntity.name).isEqualTo("john_doe"),
                () -> assertThat(persistedEntity.age).isEqualTo(30)
        );
    }

    @Test
    @DisplayName("Auto 전략을 사용하지만, id값이 없는 경우 에러가 발생한다.")
    void testPersistWithAutoIdButNoId() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithAutoId entity = new EntityManagerTestEntityWithAutoId(null, "john_doe", 30);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> entityManager.persist(entity));
        assertThat(e.getMessage()).isEqualTo("Identifier of entity " +
                EntityManagerTestEntityWithAutoId.class.getSimpleName() +
                " must be manually assigned before calling 'persist()'");
    }

    @Test
    @DisplayName("Identity 전략을 사용하지만, id값이 있는 경우 에러가 발생한다.")
    void testPersistWithIdentityIdButId() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithIdentityId entity = new EntityManagerTestEntityWithIdentityId(1L, "john_doe", 30);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> entityManager.persist(entity));
        assertThat(e.getMessage()).isEqualTo("detached entity passed to persist: " +
                EntityManagerTestEntityWithIdentityId.class.getSimpleName()
        );
    }

    @Test
    @DisplayName("이미 관리되고 있는 엔티티를 다시 저장하려고 하면 그대로 반환된다.")
    void testPersistTwice() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithAutoId entity = new EntityManagerTestEntityWithAutoId(1L, "john_doe", 30);
        entityManager.persist(entity);

        EntityManagerTestEntityWithAutoId persistedEntity = entityManager.find(EntityManagerTestEntityWithAutoId.class, 1L);
        entityManager.persist(persistedEntity);

        EntityManagerTestEntityWithAutoId found = entityManager.find(EntityManagerTestEntityWithAutoId.class, 1L);
        assertThat(found).isEqualTo(persistedEntity);
    }

    @Test
    @DisplayName("EntityManager.update()를 통해 엔티티를 수정한다.")
    void testMerge() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithAutoId entity = new EntityManagerTestEntityWithAutoId(1L, "john_doe", 30);
        entityManager.persist(entity);

        entity.name = "jane_doe";
        entity.age = 40;

        entityManager.update(entity);

        EntityManagerTestEntityWithAutoId updated = entityManager.find(EntityManagerTestEntityWithAutoId.class, 1L);

        assertAll(
                () -> assertThat(updated.id).isEqualTo(1L),
                () -> assertThat(updated.name).isEqualTo("jane_doe"),
                () -> assertThat(updated.age).isEqualTo(40)
        );
    }

    @Test
    @DisplayName("관리되고 있지 않은 엔티티를 EntityManager.update()를 통해 수정하려고 하면 그냥 저장이 실행된다.")
    void testMergeNotManagedEntity() throws Exception {
        EntityManager entityManager = new EntityManagerImpl(new JdbcTemplate(server.getConnection()), new PersistenceContextImpl());
        EntityManagerTestEntityWithAutoId entity = new EntityManagerTestEntityWithAutoId(1L, "john_doe", 30);

        entity.name = "jane_doe";
        entity.age = 40;

        entityManager.update(entity);

        EntityManagerTestEntityWithAutoId updated = entityManager.find(EntityManagerTestEntityWithAutoId.class, 1L);
        assertAll(
                () -> assertThat(updated.id).isEqualTo(1L),
                () -> assertThat(updated.name).isEqualTo("jane_doe"),
                () -> assertThat(updated.age).isEqualTo(40)
        );
    }
}
