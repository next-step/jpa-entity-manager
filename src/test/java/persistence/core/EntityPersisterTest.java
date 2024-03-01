package persistence.core;

import database.DatabaseServer;
import database.H2;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.dml.DMLQueryBuilder;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EntityPersisterTest {

    private DatabaseServer server;
    EntityPersister entityPersister;

    @BeforeEach
    public void setUp() throws SQLException {
        EntityContextManager.loadEntities();
        try {
            new DDLExcuteor().createTable(Person.class);
        } catch (RuntimeException e) {
            // ignore
        }

        server = new H2();
        server.start();

        entityPersister = new EntityPersister(server, Person.class, DMLQueryBuilder.getInstance());
    }

    @AfterEach
    public void tearDown() throws SQLException {
        new DDLExcuteor().dropTable(Person.class);
        server.stop();
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
        entityPersister.delete(select(1L));

        assertThrowsExactly(RuntimeException.class, () -> select(1L));
    }

    private <T> void insert(T entity) {
        entityPersister.insert(entity);
    }

    private <T> T select(Long id) throws Exception {
        return entityPersister.select(Person.class, id);
    }



}
