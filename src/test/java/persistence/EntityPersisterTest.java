package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DDLGenerator;
import persistence.sql.ddl.table.Table;
import persistence.sql.dml.DMLGenerator;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;
    private EntityManager entityManager;

    DDLGenerator ddlGenerator = new DDLGenerator(Person.class);
    DMLGenerator dmlGenerator = new DMLGenerator(Table.from(Person.class));

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(ddlGenerator.generateCreate());

        entityPersister = new EntityPersister(jdbcTemplate, dmlGenerator);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, dmlGenerator);
        PersistenceContext persistenceContext = new DefaultPersistenceContext();

        entityManager = new DefaultEntityManager(entityPersister, entityLoader, persistenceContext);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDrop());
        server.stop();
    }

    @Test
    @DisplayName("Person을 저장한다.")
    void insert() {
        // given
        String name = "name";
        Person person = new Person(name, 26, "email", 1);

        // when
        entityPersister.insert(person);

        // then
        Person result = entityManager.find(Person.class, 1L);
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Person 을 수정한다.")
    void update() {
        // given
        long id = 1L;
        entityPersister.insert(new Person("name", 26, "email", 1));
        Person person = entityManager.find(Person.class, id);

        String newName = "juri";
        person.changeName(newName);

        // when
        entityPersister.update(id, person);

        // then
        Person result = entityManager.find(Person.class, id);
        assertThat(result.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Person을 삭제한다.")
    void delete() {
        // given
        Person person = new Person("name", 26, "email", 1);
        entityPersister.insert(person);

        // when
        entityPersister.delete(entityManager.find(Person.class, 1L));

        // then
        Person result = entityManager.find(Person.class, 1L);
        assertThat(result).isNull();
    }
}
