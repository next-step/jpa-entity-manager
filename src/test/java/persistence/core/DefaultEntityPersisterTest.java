package persistence.core;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityPersisterTest {

    private DatabaseServer server;
    JdbcTemplate jdbcTemplate;
    DDLExcuteor ddlExcuteor;
    DefaultEntityPersister entityPersister;
    DefaultEntityLoader entityLoader;


    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);

        createTable();

        entityPersister = new DefaultEntityPersister(jdbcTemplate);
        entityLoader = new DefaultEntityLoader(jdbcTemplate);
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

    @Test
    @DisplayName("insert Test")
    public void insertTest() throws Exception {
        Person insertData = getInsertData();
        insert(insertData);

        Person select = select(insertData.getClass(), 1L);

        assertAll(
                () -> assertNotNull(select),
                () -> assertEquals(select.getId(), 1L),
                () -> assertEquals(select.getName(), "jinny"),
                () -> assertEquals(select.getAge(), 30)
        );

    }

    @Test
    @DisplayName("delete후 해당 id로 조회가 되면 안됨")
    public void deleteTest() throws Exception {
        Person insertData = getInsertData();
        insert(insertData);
        entityPersister.delete(select(insertData.getClass(), 1L));

        assertThrowsExactly(RuntimeException.class, () -> select(insertData.getClass(), 1L));
    }

    @Test
    @DisplayName("update후 entity에 변경된 정보 반영")
    public void updateTest() throws Exception {
        Person insertData = getInsertData();
        Long id = insert(getInsertData());
        insertData.setId(id);
        insertData.setAge(33);

        update(insertData);

        Person select = select(insertData.getClass(), id);
        assertAll(
            () -> assertEquals(select.getAge(), 33)
        );
    }


    private <T> Long insert(T entity) {
        return entityPersister.insert(entity);
    }

    private <T> T select(Class<T> entityClass, Long id) throws Exception {
        return entityLoader.find(entityClass, id);
    }

    private <T> void update(T entity) throws Exception {
        entityPersister.update(entity);
    }

}
