package persistence.repository;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.ddl.querybuilder.CreateQueryBuilder;

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
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        Person savedPerson = repository.save(person);
        Long personId = savedPerson.getId();

        // when
        Person updatedPerson = savedPerson.changeEmail("soo@gmail.com");
        Person changedPerson = repository.save(updatedPerson);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(changedPerson).isEqualTo(new Person(personId, "김철수", 21, "soo@gmail.com", null));
        });
    }


    @Test
    @DisplayName("JPA repository는 find 실행시 기존에 저장된 값을 가져온다.")
    void find() {
        // given
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        Person expected = repository.save(person);

        // then
        Assertions.assertThat(repository.find(Person.class, person.getId()).get()).isEqualTo(expected);
    }
}
