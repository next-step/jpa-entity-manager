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
import persistence.core.DDLExcuteor;
import persistence.core.DefaultEntityLoader;
import persistence.core.EntityManager;
import persistence.core.EntityManagerImpl;
import persistence.entity.EntityValue;
import persistence.entity.Person;
import persistence.entity.metadata.DefaultEntityMetadataReader;
import persistence.sql.dml.DMLQueryBuilder;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EntityManagerImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;
    private DDLExcuteor ddlExcuteor;

    private DatabaseServer server;
    EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);
        entityManager = new EntityManagerImpl(server, new DefaultEntityLoader(jdbcTemplate, DMLQueryBuilder.getInstance()), new EntityValue(new DefaultEntityMetadataReader()));

        createTable(Person.class);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable(Person.class);
        server.stop();
    }


    @Test
    @DisplayName("find 실행")
    public void findTest() throws Exception {
        final Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@gmail.com");

        Person persistedPerson = entityManager.persist(person);

        assertAll(
                () -> assertThat(persistedPerson).isNotNull(),
                () -> assertThat(persistedPerson.getName()).isEqualTo("jinny"),
                () -> assertThat(persistedPerson.getAge()).isEqualTo(30),
                () -> assertThat(persistedPerson.getEmail()).isEqualTo("test@gmail.com")
        );
    }

    private void createTable(Class<?> clazz) {
        ddlExcuteor.createTable(clazz);
    }

    private void dropTable(Class<?> clazz) {
        ddlExcuteor.dropTable(clazz);
    }

}
