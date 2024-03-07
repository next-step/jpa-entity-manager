package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.notcolumn.Person;
import persistence.sql.ddl.CreateQueryBuilder;

import java.util.stream.Stream;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class EntityManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerTest.class);
    DatabaseServer server;
    private JdbcTemplate jdbcTemplate;

    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
            entityManager = new EntityManagerImpl(jdbcTemplate);

            jdbcTemplate.execute(new CreateQueryBuilder(Person.class).getQuery());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(DROP_TABLE_USERS);
        server.stop();
    }

    @Test
    void find() {
        // given
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityManager.persist(person);
        // when
        Person actual = entityManager.find(Person.class, 1L);

        // then
        Person expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", null);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void persist() {
        // given & when
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        Person actual = (Person) entityManager.persist(person);

        // then
        Person expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", null);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void remove() {
        // given
        Person person_철수 = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        Person person_영희 = new Person("김영희", 15, "younghee.kim@gmail.com", 11);
        Person person_짱구 = new Person("신짱구", 15, "jjangoo.sin@gmail.com", 11);
        Stream.of(person_철수, person_영희, person_짱구).forEach(person -> entityManager.persist(person));

        // when
        Person person_철수_Id1 = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", null);
        entityManager.remove(person_철수_Id1);

        Assertions.assertThatThrownBy(() -> entityManager.find(Person.class, 1L)).isInstanceOf(RuntimeException.class);
    }
}
