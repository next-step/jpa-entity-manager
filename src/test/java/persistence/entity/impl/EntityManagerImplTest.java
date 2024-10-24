package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.domain.Person;
import persistence.entity.EntityManagerFactory;
import persistence.entity.EntityManger;
import persistence.sql.ddl.PersistentEntity;

class EntityManagerImplTest {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerImpl.class);
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
    @DisplayName("find 구현해보기")
    void findTest() throws SQLException, IllegalAccessException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(server);
        EntityManger em = entityManagerFactory.createEntityManager();

        em.getTransaction().beginTransaction();

        Person person = Person.builder()
            .name("John")
            .age(20)
            .email("john@naver.com")
            .build();

        em.persist(person);
        em.flush();

        em.getTransaction().commit();

        Person personOne = em.find(Person.class, 1L);
        Assertions.assertAll(
            () -> assertEquals(person.getName(), personOne.getName()),
            () -> assertEquals(person.getAge(), personOne.getAge()),
            () -> assertEquals(person.getEmail(), personOne.getEmail())
        );
    }

    @Test
    @DisplayName("remove 구현해보기")
    void removeTest() throws SQLException, IllegalAccessException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(server);
        EntityManger em = entityManagerFactory.createEntityManager();

        em.getTransaction().beginTransaction();

        Person person = Person.builder()
            .name("John")
            .age(20)
            .email("john@naver.com")
            .build();

        em.persist(person);
        em.remove(person);
        em.flush();

        em.getTransaction().commit();

        assertThatThrownBy(() -> em.find(Person.class, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Expected 1 result, got 0");
    }

    @Test
    @DisplayName("update 구현해보기")
    void updateTest() throws SQLException, IllegalAccessException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(server);
        EntityManger em = entityManagerFactory.createEntityManager();

        em.getTransaction().beginTransaction();

        Person person = Person.builder()
            .name("John")
            .age(20)
            .email("john@naver.com")
            .build();
        em.persist(person);
        em.flush();

        Person personOne = em.find(Person.class, 1L);
        personOne.setName("Jane");
        em.update(personOne);
        em.flush();

        em.getTransaction().commit();

        Person updatedPerson = em.find(Person.class, 1L);
        assertEquals("Jane", updatedPerson.getName());
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
