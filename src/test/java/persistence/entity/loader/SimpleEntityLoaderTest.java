package persistence.entity.loader;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonFixture;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.DmlGenerator;

@DisplayName("SimpleEntityLoader class 의")
class SimpleEntityLoaderTest {

    private DatabaseServer server;

    private JdbcTemplate jdbcTemplate;

    private DdlGenerator ddlGenerator;
    private DmlGenerator dmlGenerator;


    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlGenerator = DdlGenerator.getInstance(H2Dialect.getInstance());
        dmlGenerator = DmlGenerator.getInstance();
        jdbcTemplate.execute(ddlGenerator.generateCreateQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDropQuery(Person.class));
        server.stop();
    }

    @DisplayName("find 메서드는")
    @Nested
    class Find {
        @DisplayName("entity를 찾는다.")
        @Test
        void testFind() {
            // given
            SimpleEntityLoader loader = SimpleEntityLoader.from(jdbcTemplate);
            Person sample = PersonFixture.createPerson();
            jdbcTemplate.execute(dmlGenerator.generateInsertQuery(sample));
            // when
            Person person = loader.find(Person.class, 1L);

            // then
            assertAll(
                () -> assertThat(person.getAge()).isEqualTo(sample.getAge()),
                () -> assertThat(person.getEmail()).isEqualTo(sample.getEmail()),
                () -> assertThat(person.getName()).isEqualTo(sample.getName())
            );
        }
    }
}
