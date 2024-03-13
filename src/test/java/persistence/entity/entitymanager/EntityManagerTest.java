package persistence.entity.entitymanager;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerImpl;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.common.DtoMapper;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class EntityManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private EntityManager entityManager;

    @BeforeAll
    static void setupOnce() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
    @BeforeEach
    void setUp() {
        String query = new CreateQueryBuilder(Person.class).getQuery();
        jdbcTemplate.execute(query);

        entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(DROP_TABLE_USERS);
    }

    @AfterAll
    static void tearDownOnce() {
        server.stop();
    }

    @Test
    void find() {
        // given
        Person testFixture = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        Person expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityManager.persist(testFixture);

        // when
        Person actual = entityManager.find(Person.class, 1L).get();

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void persist() {
        // given & when
        Person testsFixture = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        Person expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityManager.persist(testsFixture);

        // then
        Person actual = entityManager.find(Person.class, 1L).get();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void remove() {
        // given
        List.of(new Person("김철수", 21, "chulsoo.kim@gmail.com", 11),
                        new Person("김영희", 15, "younghee.kim@gmail.com", 11),
                        new Person("신짱구", 15, "jjangoo.sin@gmail.com", 11))
                .forEach(person -> entityManager.persist(person));

        // when
        entityManager.remove(new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11));

        // then
        String query = new SelectQueryBuilder(Person.class).getFindAllQuery();
        List<Person> actual = jdbcTemplate.query(query, new DtoMapper<>(Person.class));
        Assertions.assertThat(actual.get(0)).isEqualTo(new Person(2L, "김영희", 15, "younghee.kim@gmail.com", 11));
        Assertions.assertThat(actual.get(1)).isEqualTo(new Person(3L, "신짱구", 15, "jjangoo.sin@gmail.com", 11));
    }
}