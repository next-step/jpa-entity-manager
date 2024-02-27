package persistence.sql.dml.builder;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.domain.Person;
import persistence.sql.ddl.builder.CreateQueryBuilder;
import persistence.sql.ddl.builder.DropQueryBuilder;
import persistence.sql.ddl.builder.QueryBuilder;
import persistence.sql.ddl.dialect.H2Dialect;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {
    private static DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
    private final RowMapper<Person> rowMapper = new RowMapperImpl<>(Person.class);

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();
    }

    @AfterAll
    static void shutDown() {
        server.stop();
    }

    @BeforeEach
    void setup() throws SQLException {
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        QueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect());
        jdbcTemplate.execute(createQueryBuilder.generateSQL(Person.class));
    }

    @AfterEach
    void clean() {
        String sql = new DropQueryBuilder(new H2Dialect()).generateSQL(Person.class);
        jdbcTemplate.execute(sql);
    }

    @Test
    @DisplayName("delete/데이터 insert2회 delete1회/findAll 데이터 1개")
    void delete() throws IllegalAccessException {
        // given insert2회
        Person person = new Person("hoon25", 20, "hoon25@gmail.com");
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(person));
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(person));

        // when delete1회
        Person findPerson = jdbcTemplate.queryForObject(selectQueryBuilder.findById(Person.class, 1L), rowMapper);
        jdbcTemplate.execute(deleteQueryBuilder.generateSQL(findPerson));

        // then findAll 데이터1개
        List<Person> persons = jdbcTemplate.query(selectQueryBuilder.findAll(Person.class), new RowMapperImpl<>(Person.class));
        assertThat(persons).hasSize(1);
    }
}
