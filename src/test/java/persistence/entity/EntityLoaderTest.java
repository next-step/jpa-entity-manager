package persistence.entity;

import database.DatabaseServer;
import database.H2;
import entity.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import persistence.sql.Query;
import persistence.sql.dialect.h2.H2Dialect;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static final Query QUERY = new Query(new H2Dialect());

    @BeforeAll
    static void beforeAll() throws Exception {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("create table users (id bigint generated by default as identity, nick_name varchar(255), old integer, email varchar(255) not null, primary key (id))");
        jdbcTemplate.execute("insert into users (id, nick_name, old, email) values (default, 'test1', 10, 'test1@gmail.com')");
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("drop table if exists users CASCADE");
    }

    @DisplayName("EntityLoader#selectById 통해 Entity를 불러온다.")
    @Test
    void selectByIdTest() {
        // given
        EntityLoader entityLoader = new EntityLoader(QUERY, jdbcTemplate);
        Person expected = new Person(1L, "test1", 10, "test1@gmail.com", 0);

        // when
        Person actual = entityLoader.selectById(expected.getClass(), 1L);

        // then
        assertThat(actual).isEqualTo(expected);
    }

}
