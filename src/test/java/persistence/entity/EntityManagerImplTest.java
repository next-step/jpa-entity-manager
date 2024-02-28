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
import persistence.sql.dml.InsertQueryBuilder;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

    private JdbcTemplate jdbcTemplate;
    private TableColumn table;
    private Dialect dialect;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityManager = new EntityManagerImpl(jdbcTemplate, new MysqlDialect());
        Class<Person> personEntity = Person.class;
        table = new TableColumn(personEntity);
        dialect = new MysqlDialect();

        createTable(personEntity);
    }

    private void createTable(Class<Person> personEntity) {
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
        Long id = 1L;
        Person person = new Person(id, "John", 25, "qwer@asdf.com", 1);

        String insertQuery = new InsertQueryBuilder(dialect).build(person);
        jdbcTemplate.execute(insertQuery);

        // when
        Person findPerson = entityManager.find(Person.class, id);

        // then
        assertAll(
                () -> assertThat(person).isNotNull(),
                () -> assertThat(findPerson.getId()).isEqualTo(person.getId()),
                () -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
                () -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @DisplayName("persist 메서드를 통해 Person 객체를 저장한다. - autho_increment인 경우 id가 1씩 증가한다.")
    @Test
    void persist() {
        // given
        Person person = new Person("John", 99, "john@test.com", 1);

        // when
        entityManager.persist(person);

        // then
        Person findPerson = entityManager.find(Person.class, person.getId());
        assertAll(
                () -> assertThat(findPerson).isNotNull(),
                () -> assertThat(findPerson.getId()).isEqualTo(1L),
                () -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
                () -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @DisplayName("entityManager.persist() 메서드를 통해 Person 객체를 저장한다. - persistContext에 저장된다.")
    @Test
    void persistContext() {
        // given
        Person person = new Person(1L, "John", 99, "john@test.com", 1);

        // when
        entityManager.persist(person);

        // then
        PersistenceContext persistContext = entityManager.getPersistContext();
        Optional<Object> findPerson = persistContext.getEntity(person.getId());

        assertAll(
                () -> assertThat(findPerson).isPresent(),
                () -> assertThat(findPerson.get()).isEqualTo(person)
        );

    }

    @DisplayName("persist 메서드를 통해 Person 객체를 저장한다. - id가 있다면 증가하지 않는다.")
    @Test
    void persistWhenHasId() {
        // given
        Person person = new Person(1L, "John", 99, "john@test.com", 1);

        // when
        entityManager.persist(person);

        // then
        Person findPerson = entityManager.find(Person.class, person.getId());
        assertAll(
                () -> assertThat(findPerson).isNotNull(),
                () -> assertThat(findPerson.getId()).isEqualTo(person.getId()),
                () -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
                () -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @DisplayName("remove 메서드를 통해 Person 객체를 삭제한다.")
    @Test
    void remove() {
        // given
        Person person = new Person(1L, "John", 99, "john@test.com", 1);
        entityManager.persist(person);

        // when
        entityManager.remove(person);

        // then
        assertThatThrownBy(() -> entityManager.find(Person.class, person.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("remove 메서드를 통해 Person 객체를 삭제한다. - persistContext에서도 삭제된다.")
    @Test
    void removeContext() {
        // given
        Person person = new Person(1L, "John", 99, "john@test.com", 1);
        entityManager.persist(person);

        // when
        entityManager.remove(person);

        // then
        PersistenceContext persistContext = entityManager.getPersistContext();
        Optional<Object> findPerson = persistContext.getEntity(person.getId());
        assertThat(findPerson).isEmpty();
    }

    @DisplayName("update 메서드를 통해 Person 객체를 수정한다.")
    @Test
    void update() {
        // given
        Person person = new Person(1L, "John", 99, "john@test.com", 1);
        entityManager.persist(person);

        // when
        person.setName("John2");
        entityManager.update(person);

        // then
        Person findPerson = entityManager.find(Person.class, person.getId());
        assertThat(findPerson.getName()).isEqualTo("John2");
    }

    @DisplayName("update 메서드를 통해 Person 객체를 수정한다. - persistContext에서 id는 그대로고 엔티티만 수정된다.")
    @Test
    void updateContext() {
        // given
        Person person = new Person(1L, "John", 99, "john@test.com", 1);
        entityManager.persist(person);

        // when
        person.setName("John2");
        entityManager.update(person);

        // then
        PersistenceContext persistContext = entityManager.getPersistContext();
        Optional<Object> findPerson = persistContext.getEntity(person.getId());
        assertAll(
                () -> assertThat(findPerson).isPresent(),
                () -> assertThat(((Person) findPerson.get()).getId()).isEqualTo(1L),
                () -> assertThat(((Person) findPerson.get()).getName()).isEqualTo("John2")
        );
    }
}
