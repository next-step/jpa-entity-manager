package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.notcolumn.Person;
import persistence.sql.ddl.CreateQueryBuilder;

import java.util.Optional;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class EntityLoaderTest {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private EntityManager entityManager;
    private EntityLoader entityLoader;

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
        entityLoader = new EntityLoader(jdbcTemplate);
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
    void findTest() {
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityManager.persist(person);

        var expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        var actual = entityLoader.find(Person.class, 1L).get();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}