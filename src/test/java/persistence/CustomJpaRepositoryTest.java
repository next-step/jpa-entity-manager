package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.CustomJpaRepository;
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

class CustomJpaRepositoryTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private DDLGenerator ddlGenerator;
    private EntityManager entityManager;
    private CustomJpaRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlGenerator = new DDLGenerator(Person.class);
        jdbcTemplate.execute(ddlGenerator.generateCreate());

        DMLGenerator dmlGenerator = new DMLGenerator(Table.from(Person.class));
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, dmlGenerator);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, dmlGenerator);
        PersistenceContext persistenceContext = new DefaultPersistenceContext();

        entityManager = new DefaultEntityManager(entityPersister, entityLoader, persistenceContext);
        repository = new CustomJpaRepository<>(entityManager);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDrop());
        server.stop();
    }

    @Test
    @DisplayName("person save 시 dirty checking 로직 구현")
    void saveWithDirty() {
        // given
        String name = "name";
        int age = 26;
        String email = "email";
        int index = 1;

        Person person = (Person) repository.save(new Person(name, age, email, index));

        String newName = "newName";
        person.changeName(newName);

        // when
        Person result = (Person) repository.save(person);

        // then
        assertThat(result.getName()).isEqualTo(newName);
    }
}
