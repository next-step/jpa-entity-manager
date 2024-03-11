package persistence.repository;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.notcolumn.Person;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.sql.ddl.CreateQueryBuilder;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class CustomJpaRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomJpaRepositoryTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;
    private CustomJpaRepository repository;

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

        persistenceContext = new PersistenceContextImpl(jdbcTemplate);
        repository = new CustomJpaRepository(jdbcTemplate);
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
    @DisplayName("snapshot은 항상 최신의 DB 값을 유지한다.")
    void saveWithDirty() {
        // given
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        var person_ID있음 = repository.save(person);
        var firstSnapshot = persistenceContext.getDatabaseSnapshot(person, 1L);

        // when
        Person updatedPerson = person_ID있음.changeEmail("soo@gmail.com");
        repository.save(updatedPerson);
        var secondSnapshot = persistenceContext.getDatabaseSnapshot(person, 1L);

        // then
        Assertions.assertThat(firstSnapshot.get()).isEqualTo(new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", null));
        Assertions.assertThat(secondSnapshot.get()).isEqualTo(new Person(1L, "김철수", 21, "soo@gmail.com", null));
    }

}
