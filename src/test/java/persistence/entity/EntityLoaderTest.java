package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.MysqlDialect;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EntityLoaderTest {

    private JdbcTemplate jdbcTemplate;
    private TableColumn table;
    private Dialect dialect;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        Class<Person> personEntity = Person.class;
        table = new TableColumn(personEntity);
        dialect = new MysqlDialect();
        Columns columns = new Columns(personEntity.getDeclaredFields(), dialect);
        IdColumn idColumn = new IdColumn(personEntity.getDeclaredFields(), dialect);

        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(table, columns, idColumn);

        String createQuery = createQueryBuilder.build();
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(table);
        String dropQuery = dropQueryBuilder.build();
        jdbcTemplate.execute(dropQuery);
    }

    @DisplayName("find 메서드를 통해 id에 해당하는 Person 객체를 찾는다.")
    @Test
    void find() {
        // given
        Person person = new Person("홍길동", "jon@test.com", 20);
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate, dialect);
        entityManager.persist(person);
        EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate, dialect);

        // when
        Person foundPerson = entityLoader.find(Person.class, 1L);

        // then
        assertAll(
                () -> assertEquals(person.getName(), foundPerson.getName()),
                () -> assertEquals(person.getEmail(), foundPerson.getEmail())
        );
    }
}
