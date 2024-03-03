package persistence.core;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityPersisterTest {

    private DatabaseServer server;
    JdbcTemplate jdbcTemplate;
    DDLExcuteor ddlExcuteor;
    DefaultEntityPersister defaultEntityPersister;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);
        EntityContextManager.loadEntities();

        createTable();

        defaultEntityPersister = new DefaultEntityPersister(jdbcTemplate);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable();
        server.stop();
    }

    private void createTable() {
        ddlExcuteor.createTable(Person.class);
    }

    private void dropTable() {
        ddlExcuteor.dropTable(Person.class);
    }

    private Person getInsertData() {
        Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@test.com");
        return person;
    }

    private Person selectedData() {
        Person person = new Person();
        person.setId(1L);
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@test.com");
        return person;
    }

    @Test
    public void insertTest() throws Exception {
        insert(getInsertData());

        Person select = select(1L);

        assertAll(
                () -> assertNotNull(select),
                () -> assertEquals(select.getId(), 1L),
                () -> assertEquals(select.getName(), "jinny"),
                () -> assertEquals(select.getAge(), 30)
        );

    }

    @Test
    public void selectTest() throws Exception {
        insert(getInsertData());

        Person select = select(1L);

        assertAll(
                () -> assertNotNull(select),
                () -> assertEquals(select.getId(), 1L),
                () -> assertEquals(select.getName(), "jinny"),
                () -> assertEquals(select.getAge(), 30)
        );

    }

    @Test
    public void deleteTest() throws Exception {
        insert(getInsertData());
        defaultEntityPersister.delete(select(1L));

        assertThrowsExactly(RuntimeException.class, () -> select(1L));
    }

    private <T> void insert(T entity) {
        defaultEntityPersister.insert(entity);
    }

    private <T> T select(Long id) throws Exception {
        return defaultEntityPersister.select(Person.class, id);
    }



}