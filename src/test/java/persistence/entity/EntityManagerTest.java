package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;
import persistence.sql.ddl.SchemaGenerator;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerTest {

    private final SchemaGenerator schemaGenerator = new SchemaGenerator(Person.class);
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(Person.class);
    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(Person.class);
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(Person.class);
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void init() throws Exception {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(schemaGenerator.generateDropTableDdlString());
        jdbcTemplate.execute(schemaGenerator.generateCreateTableDdlString());

        entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @AfterEach
    void teardown() {
        server.stop();
    }


    @DisplayName("식별자로 단일 엔티티틀 조회한다.")
    @Test
    void find() {
        jdbcTemplate.execute(insertQueryBuilder.build(new Person("jack", 20, "jack@abc.com")));

        Person person = entityManager.find(Person.class, 1L);

        assertAll("단일 Person 엔티티 조회", () -> {
            assertThat(person.getId()).isEqualTo(1L);
            assertThat(person.getName()).isEqualTo("jack");
            assertThat(person.getAge()).isEqualTo(20);
            assertThat(person.getEmail()).isEqualTo("jack@abc.com");
            assertThat(person.getIndex()).isNull();
        });
    }

    @DisplayName("단일 엔티티를 저장한다.")
    @Test
    void persist() {
        entityManager.persist(new Person("jack", 20, "jack@abc.com"));

        Person person = entityManager.find(Person.class, 1L);

        assertAll("단일 Person 엔티티 조회", () -> {
            assertThat(person.getId()).isEqualTo(1L);
            assertThat(person.getName()).isEqualTo("jack");
            assertThat(person.getAge()).isEqualTo(20);
            assertThat(person.getEmail()).isEqualTo("jack@abc.com");
            assertThat(person.getIndex()).isNull();
        });
    }

    @DisplayName("단일 엔티티를 제거한다.")
    @Test
    void remove() {
        entityManager.persist(new Person("jack", 20, "jack@abc.com"));
        Person person = entityManager.find(Person.class, 1L);
        assertThat(person).isNotNull();

        entityManager.remove(person);
        person = entityManager.find(Person.class, 1L);
        assertThat(person).isNull();
    }

}
