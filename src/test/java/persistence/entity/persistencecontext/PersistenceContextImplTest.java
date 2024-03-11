package persistence.entity.persistencecontext;

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

class PersistenceContextImplTest {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceContextImplTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private PersistenceContext persistenceContext;

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
    void getEntity() {
        // given
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        persistenceContext.addEntity(person);

        // when
        Person actual = persistenceContext.getEntity(Person.class, 1L).get();

        // then
        Person expected = Person.copyOf(person, 1L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("addEntity 실행시 1건이 정상 저장된다.")
    @Test
    void addEntity() {
        // given
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);

        // when
        persistenceContext.addEntity(person);

        // then
        Person actual = persistenceContext.getEntity(Person.class, 1L).get();
        Assertions.assertThat(actual).isEqualTo(Person.copyOf(person, 1L));
    }

    @Test
    void removeEntity() {
        // given
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        var person_id있음 = Person.copyOf(person, 1L);
        persistenceContext.addEntity(person);

        // when
        var entityBeforeDelete = persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.removeEntity(person_id있음);
        var entityAfterDelete = persistenceContext.getEntity(Person.class, 1L);

        // then
        Assertions.assertThat(entityBeforeDelete.get()).isEqualTo(person_id있음);
        Assertions.assertThat(entityAfterDelete).isEqualTo(Optional.empty());
    }
}
