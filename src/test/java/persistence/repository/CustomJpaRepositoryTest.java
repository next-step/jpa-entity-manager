package persistence.repository;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerImpl;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.ddl.CreateQueryBuilder;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class CustomJpaRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomJpaRepositoryTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
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
    @DisplayName("JPA repository는 id가 이미 있는 entity를 저장시 update 한다.")
    void saveWithDirty() {
        // given
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        var savedPerson = repository.save(person);
        var personId = savedPerson.getId();

        // when
        var updatedPerson = savedPerson.changeEmail("soo@gmail.com");
        repository.save(updatedPerson);

        // then
        Assertions.assertThat(repository.find(Person.class, personId).get()).isEqualTo(new Person(personId, "김철수", 21, "soo@gmail.com", null));
    }

}
