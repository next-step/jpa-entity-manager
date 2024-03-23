package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DDLGenerator;
import persistence.sql.ddl.table.Table;
import persistence.sql.dml.DMLGenerator;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class DefaultEntityManagerTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private EntityManager entityManager;

    DDLGenerator ddlGenerator = new DDLGenerator(Person.class);
    DMLGenerator dmlGenerator = new DMLGenerator(Table.from(Person.class));
    PersistenceContext persistenceContext = new DefaultPersistenceContext();

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(ddlGenerator.generateCreate());

        entityPersister = new EntityPersister(jdbcTemplate, dmlGenerator);
        entityLoader = new EntityLoader(jdbcTemplate, dmlGenerator);
        DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext();
        entityManager = new DefaultEntityManager(entityPersister, entityLoader, persistenceContext);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDrop());
        server.stop();
    }

    @Test
    @DisplayName("Person 을 조회한다.")
    void find_1() {
        // given
        Person person = entityManager.persist(new Person("name", 26, "email", 1));

        // when
        Person result = entityManager.find(Person.class, person.getId());

        // then
        assertThat(result.getId()).isEqualTo(person.getId());
    }

    @Test
    @DisplayName("존재하지 않는 id로 조회할 경우 null을 반환한다.")
    void find_2() {
        // given
        long id = 1L;

        // when
        Person person = entityManager.find(Person.class, id);

        // then
        assertThat(person).isNull();
    }

    @Test
    @DisplayName("id가 null 일 경우 예외가 발생한다.")
    void find_3() {
        // given
        Long id = null;

        // when
        Throwable throwable = catchThrowable(() -> entityManager.find(Person.class, id));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[EntityManager] find: id is null");
    }

    @Test
    @DisplayName("Person 을 저장한다.")
    void persist_1() {
        // given
        String name = "name";
        Person person = new Person(name, 26, "email", 1);

        // when
        entityManager.persist(person);

        // then
        List<Person> people = jdbcTemplate.query(dmlGenerator.generateFindAll(), new DefaultRowMapper<>(Person.class));

        assertThat(people).hasSize(1);
        assertThat(people.get(0).getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("persist 할 Object 가 Entity 가 아닐 경우, 예외가 발생한다.")
    void persist_2() {
        // given
        entityManager = new DefaultEntityManager(entityPersister, entityLoader, persistenceContext);

        // when
        Throwable throwable = catchThrowable(() -> entityManager.persist(new NotEntity(1L)));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[EntityManager] persist: the instance is not an entity");
    }

    @Test
    @DisplayName("Person 을 삭제한다.")
    void remove_1() {
        // given
        long id = 1L;
        entityManager.persist(new Person(id, "name", 26, "email"));

        Person person = entityManager.find(Person.class, id);

        // when
        entityManager.remove(person);

        // then
        Person result = entityManager.find(Person.class, id);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("remove 할 Object 가 Entity 가 아닐 경우, 예외가 발생한다.")
    void remove_2() {
        // given
        entityManager = new DefaultEntityManager(entityPersister, entityLoader, persistenceContext);

        // when
        Throwable throwable = catchThrowable(() -> entityManager.remove(new NotEntity(1L)));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[EntityManager] persist: the instance is not an entity");
    }

    @Test
    @DisplayName("getCachedDatabaseSnapshot 에 저장 후 merge 호출하여 더티체킹")
    void merge() {
        // given
        Person person = new Person("name", 26, "email", 1);
        Person persistedPerson = entityManager.persist(person);
        entityManager.find(Person.class, persistedPerson.getId());

        person.changeName("juri");

        // when
        Person mergedPerson = entityManager.merge(persistedPerson.getId(), person);

        // then
        assertThat(mergedPerson.getName()).isEqualTo("juri");
    }
}
