package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
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
import persistence.entity.impl.EntityManagerFactoryImpl;
import persistence.entity.impl.EntityManagerImpl;
import persistence.sql.ddl.PersistentEntity;

public class CustomJpaRepositoryTest {
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
    @DisplayName("save 시 dirty checking 로직 테스트")
    void saveWithDirty() throws SQLException, IllegalAccessException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(server);
        EntityManager em = entityManagerFactory.createEntityManager();

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

        em.getTransaction().commit();

        Person updatedPerson = em.find(Person.class, 1L);
        assertEquals("Jane", updatedPerson.getName());
    }

    @Test
    @DisplayName("dirty checking 시 null column 테스트")
    void saveWithDirtyWithNullColumn() throws SQLException, IllegalAccessException {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(server);
        EntityManager em = entityManagerFactory.createEntityManager();

        em.getTransaction().beginTransaction();

        Person person = Person.builder()
            .name("John")
            .age(20)
            .email("john@naver.com")
            .build();
        em.persist(person);
        em.flush();

        Person personOne = em.find(Person.class, 1L);
        personOne.setAge(null);

        em.getTransaction().commit();

        Person updatedPerson = em.find(Person.class, 1L);
        assertNull(updatedPerson.getAge());
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
