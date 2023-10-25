package hibernate.entity;

import database.DatabaseServer;
import database.H2;
import hibernate.ddl.CreateQueryBuilder;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityPersisterTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister = new EntityPersister(jdbcTemplate);

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateQueryBuilder.INSTANCE.generateQuery(EntityClass.getInstance(TestEntity.class)));
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("truncate table test_entity;");
    }

    @AfterAll
    static void afterAll() {
        jdbcTemplate.execute("drop table test_entity;");
        server.stop();
    }

    @Test
    void update_쿼리를_실행한다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "영진최");
        jdbcTemplate.execute("insert into test_entity (id, nick_name) values (1, '최진영');");

        // when
        boolean actual = entityPersister.update(givenEntity);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void insert_쿼리를_실행한다() {
        // given
        TestEntity givenEntity = new TestEntity("최진영");

        // when
        entityPersister.insert(givenEntity);
        Integer actual = testEntityCount();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void delete_쿼리를_실행한다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "최진영");
        jdbcTemplate.execute("insert into test_entity (id, nick_name) values (1, '최진영');");

        // when
        entityPersister.delete(givenEntity);
        Integer actual = testEntityCount();

        // then
        assertThat(actual).isEqualTo(0);
    }

    private Integer testEntityCount() {
        return jdbcTemplate.queryForObject("select count(*) from test_entity", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet) {
                try {
                    resultSet.next();
                    return resultSet.getInt(1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public TestEntity(String name) {
            this.name = name;
        }
    }

    static class ErrorEntity {
    }
}
