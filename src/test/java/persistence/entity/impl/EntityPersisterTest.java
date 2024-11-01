package persistence.entity.impl;

import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityRowMapper;
import persistence.sql.ddl.CreateTableQueryBuilder;
import persistence.sql.ddl.DropTableQueryBuilder;
import persistence.sql.ddl.QueryBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EntityPersisterTest {
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;
    private EntityLoader<TestEntity> entityLoader;
    private final Long testId = 1L;
    private final TestEntity testEntity = new TestEntity(testId, "Test Entity");

    @BeforeEach
    void setUp() throws SQLException {
        H2 databaseServer = new H2();
        databaseServer.start();
        Connection connection = databaseServer.getConnection();
        // JdbcTemplate을 실제로 구현한 클래스를 사용하거나 간단한 구현을 작성
        jdbcTemplate = new JdbcTemplate(connection);
        entityPersister = new EntityPersister(jdbcTemplate);
        entityLoader = new EntityLoader<>(jdbcTemplate);
        QueryBuilder ddlQueryBuilder = new CreateTableQueryBuilder(TestEntity.class);
        String createTableQuery = ddlQueryBuilder.executeQuery();
        jdbcTemplate.execute(createTableQuery);
    }

    @AfterEach
    void tearDown() throws SQLException {
        QueryBuilder ddlQueryBuilder = new DropTableQueryBuilder(TestEntity.class);
        String dropTableQuery = ddlQueryBuilder.executeQuery();
        jdbcTemplate.execute(dropTableQuery);
    }

    @Test
    void testInsertEntity() {
        Long insertedId = entityPersister.insert(testEntity);

        assertNotNull(insertedId);
        assertEquals(testId, insertedId);
    }

    @Test
    void testUpdateEntity() {
        jdbcTemplate.executeInsert("INSERT INTO TestEntity (id, name) VALUES (1, 'Test Entity')");

        testEntity.setName("Updated Entity");
        entityPersister.update(testEntity);
        TestEntity updatedEntity = entityLoader.load(TestEntity.class, testId);

        assertNotNull(updatedEntity);
        assertEquals("Updated Entity", updatedEntity.getName());
    }

    @Test
    void testRemoveEntity() {
        jdbcTemplate.executeInsert("INSERT INTO TestEntity (id, name) VALUES (1, 'Test Entity')");

        entityPersister.remove(TestEntity.class, testId);
       assertThrows(RuntimeException.class, ()->
               jdbcTemplate.queryForObject("SELECT * FROM TestEntity WHERE id = 1", new EntityRowMapper<>(TestEntity.class)));

    }

    // TestEntity: 단순한 엔티티 클래스
    @Entity
    public static class TestEntity {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public TestEntity() {
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestEntity)) return false;
            TestEntity that = (TestEntity) o;
            return id.equals(that.id) && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}