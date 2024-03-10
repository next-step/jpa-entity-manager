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

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class EntityPersisterTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityPersisterTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static EntityPersister entityPersister;
    private static EntityManager entityManager;

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
        entityPersister = new EntityPersister(jdbcTemplate);
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
    @DisplayName("insert 시 Person 1건 저장된다.")
    void persistTest() {
        // given
        var testFixture = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);

        // when
        entityPersister.insert(testFixture);

        // then
        Person actual = entityManager.find(Person.class, 1L).get();
        Person expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("update 성공시 true가 리턴된다.")
    void updateTest() {
        // given
        var testFixture = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityPersister.insert(testFixture);

        // when
        boolean isUpdated = entityPersister.update(testFixture, 1L);

        // then
        Assertions.assertThat(isUpdated).isTrue();
    }

    @Test
    @DisplayName("delete 성공시, 재조회시 데이터가 존재하지 않는다.")
    void deleteTest() {
        // given
        var person_아이디없음 = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        var person_아이디있음 = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityPersister.insert(person_아이디없음);

        // when
        entityPersister.delete(person_아이디있음);

        // then
        Assertions.assertThat(entityManager.find(Person.class, 1L)).isEmpty();
    }
}
