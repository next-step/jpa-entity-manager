package hibernate.entity;

import database.DatabaseServer;
import database.H2;
import hibernate.ddl.CreateQueryBuilder;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EntityLoaderTest {


    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private final EntityLoader<TestEntity> entityLoader = new EntityLoader<>(TestEntity.class, jdbcTemplate);

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateQueryBuilder.INSTANCE.generateQuery(new EntityClass<>(EntityManagerImplTest.TestEntity.class)));
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
    void find_쿼리를_실행한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (1, '최진영', 19)");

        // when
        TestEntity actual = entityLoader.find(TestEntity.class, 1L);

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("최진영"),
                () -> assertThat(actual.age).isEqualTo(19)
        );
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name")
        private String name;

        private Integer age;

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
}
