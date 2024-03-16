package persistence.entity.impl;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.Arguments;
import persistence.sql.QueryBuilder;
import persistence.sql.ddl.entity.Person;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractJdbcTemplateTest {
    protected static DatabaseServer server;

    private static final AtomicLong id = new AtomicLong(1);

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();

        server.start();

        createTable();
    }

    @AfterAll
    static void afterAll() throws SQLException {
        dropTable();

        server.stop();
    }

    protected static long getCurrentId() {
        return id.get();
    }

    protected static long getId() {
        return id.getAndIncrement();
    }

    protected final static Map<Long, Person> idToPersonMap = Stream.of(
        new Person(getId(), "root", 20, "root@gmail.com"),
        new Person(getId(), "test", 30, "test@gmail.com"),
        new Person(getId(), "user1", 40, "user1@gmail.com"),
        new Person(getId(), "user2", 50, "user2@gmail.com")
    ).collect(Collectors.toMap(Person::getId, Function.identity()));

    protected static void dropTable() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        QueryBuilder queryBuilder = new QueryBuilder();

        jdbcTemplate.execute(queryBuilder.getDropTableQuery(Person.class));

        id.set(1L);
    }

    protected static void createTable() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        QueryBuilder queryBuilder = new QueryBuilder();

        jdbcTemplate.execute(queryBuilder.getCreateTableQuery(Person.class));
    }

    protected static void deleteTable() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        QueryBuilder queryBuilder = new QueryBuilder();

        jdbcTemplate.execute(queryBuilder.getDeleteAllQuery(Person.class));
    }

    protected static void initializeTable() throws SQLException {
        for (Person person : idToPersonMap.values()) {
            insertTable(person);
        }
    }

    protected static void insertTable(Person person) throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        QueryBuilder queryBuilder = new QueryBuilder();

        jdbcTemplate.execute(queryBuilder.getInsertQuery(person));

        getId();
    }

    protected static Integer selectCountOfTable() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        QueryBuilder queryBuilder = new QueryBuilder();

        return jdbcTemplate.queryForObject(queryBuilder.getSelectCountQuery(Person.class),
            resultSet -> resultSet.getInt(1));
    }

    protected static JdbcTemplate getJdbcTemplate() throws SQLException {
        return new JdbcTemplate(server.getConnection());
    }

    protected static Stream<Arguments> providePerson() {
        return idToPersonMap.values().stream()
            .map(Arguments::of);
    }

    protected static Stream<Arguments> providePersonId() {
        return idToPersonMap.values().stream()
            .map(Person::getId)
            .map(Arguments::of);
    }
}
