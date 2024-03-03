package persistence.core;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.dml.DMLQueryBuilder;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityLoaderTest {

    DatabaseServer server;
    EntityPersister entityPersister;
    EntityLoader entityLoader;
    DDLExcuteor ddlExcuteor;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);
        entityPersister = new DefaultEntityPersister(jdbcTemplate);
        entityLoader = new DefaultEntityLoader(jdbcTemplate, DMLQueryBuilder.getInstance());

        EntityContextManager.loadEntities();

        createTable();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable();
        server.stop();
    }

    @Test
    public void findAllTest() throws Exception {
        insertSampleData(5);

        List<Person> people = entityLoader.find(Person.class);

        assertEquals(5, people.size());
    }

    @Test
    public void findTest() throws Exception {
        insertSampleData(5);

        Person select = entityLoader.find(Person.class, 2L);

        assertAll(
                () -> assertNotNull(select),
                () -> assertEquals(select.getId(), 2L),
                () -> assertEquals(select.getName(), "jinny_1"),
                () -> assertEquals(select.getAge(), 31)
        );

    }

    private void insertSampleData(int count) {
        for (int i = 0; i < count; i++) {
            Person person = new Person();
            person.setName("jinny_" + i);
            person.setAge(30 + i);
            person.setEmail("test@test.com");

            entityPersister.insert(person);
        }
    }


    private void createTable() {
        ddlExcuteor.createTable(Person.class);
    }

    private void dropTable() {
        ddlExcuteor.dropTable(Person.class);
    }

}
