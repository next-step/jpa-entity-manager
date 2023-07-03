package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;
import persistence.sql.ddl.SchemaGenerator;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersonJpaRepositoryTest {

    private final SchemaGenerator schemaGenerator = new SchemaGenerator(Person.class);
    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(Person.class);
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(Person.class);
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;
    private PersonJpaRepository repository;

    @BeforeEach
    void setup() throws Exception {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(schemaGenerator.generateDropTableDdlString());
        jdbcTemplate.execute(schemaGenerator.generateCreateTableDdlString());

        entityManager = new EntityManagerImpl(jdbcTemplate);
        repository = new PersonJpaRepository(entityManager);
    }

    @AfterEach
    void teardown() {
        server.stop();
    }

    @DisplayName("새로운 엔티티를 저장한다.")
    @Test
    void save() {
        Person savedPerson = repository.save(new Person("Kevin", 33, "kevin@abc.com"));

        String query = selectQueryBuilder.buildFindByIdQuery(savedPerson.getId());
        Person foundPerson = jdbcTemplate.queryForObject(query, new EntityRowMapper<>(Person.class));

        assertAll("새로운 엔티티 저장 결과", () -> {
            assertThat(savedPerson.getName()).isEqualTo(foundPerson.getName());
            assertThat(savedPerson.getAge()).isEqualTo(foundPerson.getAge());
            assertThat(savedPerson.getEmail()).isEqualTo(foundPerson.getEmail());
        });
    }

    @DisplayName("변경된 엔티티를 저장한다.")
    @Test
    void merge() {
        Person savedPerson = repository.save(new Person("Kevin", 33, "kevin@abc.com"));

        savedPerson.setName("Harry");
        savedPerson.setAge(40);
        savedPerson.setEmail("harry@abc.com");
        repository.save(savedPerson);
        
        String query = selectQueryBuilder.buildFindByIdQuery(savedPerson.getId());
        Person foundPerson = jdbcTemplate.queryForObject(query, new EntityRowMapper<>(Person.class));

        assertAll("Dirty Checking 동작 결과", () -> {
            assertThat(foundPerson.getName()).isEqualTo("Harry");
            assertThat(foundPerson.getAge()).isEqualTo(40);
            assertThat(foundPerson.getEmail()).isEqualTo("harry@abc.com");
        });
    }
}