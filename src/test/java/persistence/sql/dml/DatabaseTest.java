package persistence.sql.dml;

import database.DatabaseServer;
import database.H2;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;
import persistence.sql.ddl.SchemaGenerator;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DatabaseTest {

    private final SchemaGenerator schemaGenerator = new SchemaGenerator(Person.class);
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(Person.class);
    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(Person.class);
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(Person.class);
    private final EntityRowMapper<Person> rowMapper = new EntityRowMapper<>(Person.class);
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(schemaGenerator.generateDropTableDdlString());
        jdbcTemplate.execute(schemaGenerator.generateCreateTableDdlString());
    }

    @AfterEach
    void teardown() {
        server.stop();
    }

    @DisplayName("INSERT 쿼리 실행 시 엔티티가 DB에 저장된다.")
    @Test
    void insert() {
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("jack", 20, "jack@abc.com")));
        Person person = jdbcTemplate.queryForObject(selectQueryBuilder.buildFindByIdQuery(1L), rowMapper);

        assertAll("저장된 Person 조회", () -> {
            assertThat(person.getId()).isEqualTo(1L);
            assertThat(person.getName()).isEqualTo("jack");
            assertThat(person.getAge()).isEqualTo(20);
            assertThat(person.getEmail()).isEqualTo("jack@abc.com");
            assertThat(person.getIndex()).isNull();
        });
    }

    @DisplayName("DB에 저장된 모든 엔티티를 조회한다.")
    @Test
    void findAll() {
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("jack", 20, "jack@abc.com")));
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("kevin", 30, "kevin@abc.com")));
        List<Person> persons = jdbcTemplate.query(selectQueryBuilder.buildFindAllQuery(), rowMapper);

        assertThat(persons).hasSize(2);
    }

    @DisplayName("식별자로 하나의 엔티티를 조회한다.")
    @Test
    void findById() {
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("jack", 20, "jack@abc.com")));
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("kevin", 30, "kevin@abc.com")));
        Person person = jdbcTemplate.queryForObject(selectQueryBuilder.buildFindByIdQuery(2L), rowMapper);

        assertAll("저장된 Person 조회", () -> {
            assertThat(person.getId()).isEqualTo(2L);
            assertThat(person.getName()).isEqualTo("kevin");
            assertThat(person.getAge()).isEqualTo(30);
            assertThat(person.getEmail()).isEqualTo("kevin@abc.com");
            assertThat(person.getIndex()).isNull();
        });
    }

    @DisplayName("DB에 저장된 모든 엔티티를 삭제한다.")
    @Test
    void deleteAll() {
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("jack", 20, "jack@abc.com")));
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("kevin", 30, "kevin@abc.com")));
        List<Person> persons = jdbcTemplate.query(selectQueryBuilder.buildFindAllQuery(), rowMapper);
        assertThat(persons).hasSize(2);

        jdbcTemplate.execute(deleteQueryBuilder.buildDeleteAllQuery());
        persons = jdbcTemplate.query(selectQueryBuilder.buildFindAllQuery(), rowMapper);
        assertThat(persons).isEmpty();
    }

    @DisplayName("식별자로 하나의 엔티티를 삭제한다.")
    @Test
    void deleteById() {
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("jack", 20, "jack@abc.com")));
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("kevin", 30, "kevin@abc.com")));
        List<Person> persons = jdbcTemplate.query(selectQueryBuilder.buildFindAllQuery(), rowMapper);
        assertThat(persons).hasSize(2);

        jdbcTemplate.execute(deleteQueryBuilder.buildDeleteByIdQuery(1L));
        persons = jdbcTemplate.query(selectQueryBuilder.buildFindAllQuery(), rowMapper);
        assertThat(persons).hasSize(1);
    }
}
