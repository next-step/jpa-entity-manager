package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.query.CreateQueryBuilder;
import persistence.sql.ddl.query.DropQueryBuilder;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityPersisterTest {

    @Entity
    private static class QueryTestEntity {
        @Id
        private Long id;

        @Column(name = "nick_name", length = 60)
        private String name;

        private Integer age;

        public QueryTestEntity() {
        }

        public QueryTestEntity(Long id) {
            this.id = id;
        }

        public QueryTestEntity(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        String query = new CreateQueryBuilder(new H2Dialect()).build(QueryTestEntity.class);
        jdbcTemplate.execute(query);
    }

    @AfterEach
    void tearDown() throws SQLException {
        String query = new DropQueryBuilder().build(QueryTestEntity.class);
        jdbcTemplate.execute(query);
        server.stop();
    }

    @Test
    void shouldExecuteInsert() {
        QueryTestEntity entity = new QueryTestEntity(1L, "John", 25);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class, jdbcTemplate);

        persister.insert(entity);

        EntityManagerImpl em = new EntityManagerImpl(jdbcTemplate, new PersistenceContextImpl());
        QueryTestEntity saved = em.find(QueryTestEntity.class, 1L);
        assertAll(
                () -> assertThat(saved.id).isEqualTo(1L),
                () -> assertThat(saved.name).isEqualTo("John"),
                () -> assertThat(saved.age).isEqualTo(25)
        );
    }

    @Test
    void shouldExecuteInsertWithNullValue() {
        QueryTestEntity entity = new QueryTestEntity(1L);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class, jdbcTemplate);

        persister.insert(entity);

        EntityManagerImpl em = new EntityManagerImpl(jdbcTemplate, new PersistenceContextImpl());
        QueryTestEntity saved = em.find(QueryTestEntity.class, 1L);
        assertAll(
                () -> assertThat(saved.id).isEqualTo(1L),
                () -> assertThat(saved.name).isNull(),
                () -> assertThat(saved.age).isNull()
        );
    }

    @Test
    void shouldExecuteUpdate() {
        QueryTestEntity entity = new QueryTestEntity(1L, "John", 25);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class, jdbcTemplate);

        persister.insert(entity);

        QueryTestEntity updatedEntity = new QueryTestEntity(1L, "Chanho", 30);
        persister.update(updatedEntity);

        EntityManagerImpl em = new EntityManagerImpl(jdbcTemplate, new PersistenceContextImpl());
        QueryTestEntity updated = em.find(QueryTestEntity.class, 1L);

        assertAll(
                () -> assertThat(updated.id).isEqualTo(1L),
                () -> assertThat(updated.name).isEqualTo("Chanho"),
                () -> assertThat(updated.age).isEqualTo(30)
        );
    }

    @Test
    void shouldExecuteDelete() {
        QueryTestEntity entity = new QueryTestEntity(1L);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class, jdbcTemplate);

        persister.insert(entity);
        persister.delete(entity);

        EntityManagerImpl em = new EntityManagerImpl(jdbcTemplate, new PersistenceContextImpl());
        assertThrows(RuntimeException.class, () -> em.find(QueryTestEntity.class, 1L));
    }
}
