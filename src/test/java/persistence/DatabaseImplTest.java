package persistence;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import persistence.person.DatabasePerson;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.ddl.QueryDdl;
import persistence.sql.dml.QueryDml;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseImplTest {

    private DatabaseImpl database;
    private DatabaseServer server;
    private Class<DatabasePerson> tClass;

    @BeforeEach
    void init() throws SQLException {
        server = new H2();
        server.start();
        tClass = DatabasePerson.class;

        database = new DatabaseImpl(server.getConnection());
        createTable(tClass);
    }

    @Test
    @Order(1)
    @DisplayName("database 인터페이스를 통한 insert 성공")
    void insert() throws SQLException {
        //given
        final Long id = 3L;
        final String name = "name";
        final int age = 30;
        final String email = "zz";
        final Integer index = 1;

        DatabasePerson databasePerson = new DatabasePerson(id, name, age, email, index);

        //when & then
        assertDoesNotThrow(() -> insert(databasePerson));
    }

    @Test
    @Order(2)
    @DisplayName("database 인터페이스를 통한 findAll로 모든 list 조회")
    void findAll() throws SQLException {
        //given
        final String methodName = "findAll";

        final int count = 10;
        for (int i = count; i < 20; i++) {
            insert(new DatabasePerson(Integer.toUnsignedLong(i), "name", 30, "email", 1));
        }

        //when
        ResultSet resultSet = findAll(tClass, methodName);
        resultSet.last();

        //then
        assertThat(resultSet.getRow()).isEqualTo(count);
    }

    @Test
    @Order(3)
    @DisplayName("database 인터페이스를 통한 findById로 특정 값 가져오기")
    void findById() throws SQLException {
        //given
        final String methodName = "findById";

        final Long id = 99L;
        insert(new DatabasePerson(id, "name", 30, "email", 1));

        //when
        ResultSet resultSet = find(tClass, methodName, id);
        resultSet.last();

        //then
        assertThat(resultSet.getRow()).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("database 인터페이스를 통한 delete로 값 삭제")
    void delete() throws SQLException {
        //given
        final Long id = 9999L;
        DatabasePerson person = new DatabasePerson(id, "name", 30, "email", 1);

        insert(person);

        final TableName tableName = TableName.of(tClass);
        final Columns columns = Columns.of(tClass.getDeclaredFields());

        //when & then
        assertDoesNotThrow(() -> delete(tableName, columns, id));
    }

    @AfterEach
    void after() throws SQLException {
        dropTable(tClass);
    }

    private <T> void createTable(Class<T> tClass) throws SQLException {
        database.execute(QueryDdl.create(tClass));
    }

    private <T> void dropTable(Class<T> tClass) throws SQLException {
        database.execute(QueryDdl.drop(tClass));
    }

    private <T> void insert(T t) throws SQLException {
        final TableName tableName = TableName.of(t.getClass());
        final Columns columns = Columns.of(t.getClass().getDeclaredFields());

        database.execute(QueryDml.insert(tableName, columns, t));
    }

    private void delete(TableName tableName, Columns columns, Object args) throws SQLException {
        database.execute(QueryDml.delete(tableName, columns, args));
    }

    private <T> ResultSet findAll(Class<T> tClass, String methodName) throws SQLException {
        final TableName tableName = TableName.of(tClass);
        final Columns columns = Columns.of(tClass.getDeclaredFields());

        return database.executeQuery(QueryDml.select(methodName, tableName, columns, null));
    }

    private <T> ResultSet find(Class<T> tClass, String methodName, Object... args) throws SQLException {
        final TableName tableName = TableName.of(tClass);
        final Columns columns = Columns.of(tClass.getDeclaredFields());

        return database.executeQuery(QueryDml.select(methodName, tableName, columns, args));
    }
}