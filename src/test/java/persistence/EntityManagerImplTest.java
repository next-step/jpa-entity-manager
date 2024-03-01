package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.core.EntityManager;
import persistence.core.EntityManagerImpl;
import persistence.entity.Person;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.ddl.DDLQueryBuilderFactory;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EntityManagerImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;
    private DDLQueryBuilder ddlQueryBuilder;

    private DatabaseServer server;
    EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlQueryBuilder = DDLQueryBuilderFactory.getDDLQueryBuilder(server);
        entityManager = new EntityManagerImpl(server);

        createTable(Person.class);
    }


    @Test
    @DisplayName("find 실행")
    public void findTest() throws Exception {
        final Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@gmail.com");

        entityManager.persist(person);

        Person persistedPerson = entityManager.find(Person.class, 1L);

        assertAll(
                () -> assertThat(persistedPerson).isNotNull(),
                () -> assertThat(persistedPerson.getName()).isEqualTo("jinny"),
                () -> assertThat(persistedPerson.getAge()).isEqualTo(30),
                () -> assertThat(persistedPerson.getEmail()).isEqualTo("test@gmail.com")
        );
    }

    private void createTable(Class<?> clazz) {
        jdbcTemplate.execute(ddlQueryBuilder.createTableQuery(clazz));
    }

}
