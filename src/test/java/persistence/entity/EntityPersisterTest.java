package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.domain.Person;
import persistence.sql.ddl.PersistentEntity;

class EntityPersisterTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityPersisterTest.class);
    private static JdbcTemplate jdbcTemplate;
    private static DatabaseServer server;

    @BeforeAll
    public static void setUp() {
        try {
            server = new H2();
            server.start();

            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        }
    }

    @BeforeEach
    void createTable() {
        PersistentEntity entity = new PersistentEntity(jdbcTemplate);
        entity.createTable(Person.class);
    }

    @Test
    @DisplayName("find 구현해보기 && insert 구현해보기")
    void findInsertTest() throws IllegalAccessException {
        EntityPersister<Person> entityPersister = new EntityPersister<>(Person.class, jdbcTemplate);
        Person person = Person.builder()
                .name("John")
                .age(20)
                .email("john@naver.com")
                .build();

        entityPersister.insert(person);
        Person personOne = entityPersister.findById(1L);

        assertAll(
            () -> assertThat(personOne.getId()).isNotNull(),
            () -> assertThat(personOne.getName()).isEqualTo("John"),
            () -> assertThat(personOne.getAge()).isEqualTo(20),
            () -> assertThat(personOne.getEmail()).isEqualTo("john@naver.com")
        );
    }

    @Test
    @DisplayName("delete 구현해보기")
    void deleteTest() throws IllegalAccessException {
        EntityPersister<Person> entityPersister = new EntityPersister<>(Person.class, jdbcTemplate);
        Person person = Person.builder()
            .name("John")
            .age(20)
            .email("john@naver.com")
            .build();
        entityPersister.insert(person);
        Person personOne = entityPersister.findById(1L);
        entityPersister.delete(personOne);

        assertThatThrownBy(() -> entityPersister.findById(1L)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Expected 1 result, got 0");
    }

    @Test
    @DisplayName("update 구현해보기")
    void updateTest() throws IllegalAccessException {
        EntityPersister<Person> entityPersister = new EntityPersister<>(Person.class, jdbcTemplate);
        Person person = Person.builder()
            .name("John")
            .age(20)
            .email("john@naver.com")
            .build();
        entityPersister.insert(person);
        Person personOne = entityPersister.findById(1L);

        personOne.setName("Jane");
        entityPersister.update(personOne);

        Person updatedPerson = entityPersister.findById(1L);
        assertThat(updatedPerson.getName()).isEqualTo("Jane");

    }

    @AfterEach
    void dropTable() {
        PersistentEntity entity = new PersistentEntity(jdbcTemplate);
        entity.dropTable(Person.class);
    }

    @AfterAll
    public static void afterAllTests() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        }
    }
}
