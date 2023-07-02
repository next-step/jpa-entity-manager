package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.H2DbDialect;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.ddl.JavaToSqlColumnParser;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRepositoryTest {

    private final DatabaseServer server = new H2();
    private JdbcTemplate jdbcTemplate;
    private MyEntityManager entityManager;

    PersonRepositoryTest() throws SQLException {
    }

    @BeforeEach
    void setUp() throws SQLException {
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityManager = new MyEntityManager(new MyEntityPersister(jdbcTemplate));
        createTable();
    }

    @AfterEach
    void tearDown() {
        dropTable();
        server.stop();
    }

    @DisplayName("더티체킹 구현 테스트")
    @Test
    void dirtyCheckTest() {
        final PersonRepository personRepository = new PersonRepository(entityManager);

        final Person 정원_15살 = new Person(1L, "정원", 15, "a@a.com", 1);
        personRepository.save(정원_15살);

        final Person 정원_20살 = 정원_15살.changeAge(20);
        personRepository.save(정원_20살);

        final Person person = personRepository.findById(1L);
        assertThat(person.getAge()).isEqualTo(20);
    }

    private void createTable() {
        final JavaToSqlColumnParser javaToSqlColumnParser = new JavaToSqlColumnParser(new H2DbDialect());
        final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(javaToSqlColumnParser, Person.class);
        final String createTableSql = ddlQueryBuilder.createTable();
        jdbcTemplate.execute(createTableSql);
    }

    private void dropTable() {
        final JavaToSqlColumnParser javaToSqlColumnParser = new JavaToSqlColumnParser(new H2DbDialect());
        final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(javaToSqlColumnParser, Person.class);
        final String dropTableSql = ddlQueryBuilder.dropTable();
        jdbcTemplate.execute(dropTableSql);
    }
}
