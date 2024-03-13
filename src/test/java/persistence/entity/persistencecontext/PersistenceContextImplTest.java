package persistence.entity.persistencecontext;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.testfixture.basic.Dog;
import persistence.entity.testfixture.notcolumn.Person;
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
        persistenceContext = new PersistenceContextImpl();

        String queryPerson = new CreateQueryBuilder(Person.class).getQuery();
        jdbcTemplate.execute(queryPerson);
        String queryDog = new CreateQueryBuilder(Dog.class).getQuery();
        jdbcTemplate.execute(queryDog);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(DROP_TABLE_USERS);
    }

    @AfterAll
    static void tearDownOnce() {
        server.stop();
    }

    @DisplayName("2번 저장후 getEntity실행시 2개의 객체가 조회된다.")
    @Test
    void getEntityTwoResults() {
        // given
        var person = new Person(1L,"김철수", 21, "chulsoo.kim@gmail.com", 11);
        var dog = new Dog(1L, "바둑이");
        persistenceContext.addEntity(person);
        persistenceContext.addEntity(dog);

        // when
        var actualPerson = persistenceContext.getEntity(Person.class, 1L).get();
        var actualDog = persistenceContext.getEntity(Dog.class, 1L).get();

        // then
        Assertions.assertThat(actualPerson).isSameAs(person);
        Assertions.assertThat(actualDog).isSameAs(dog);
    }

    @DisplayName("존재하지 않는 값을 조회시, 리턴 객체는 빈값이다.")
    @Test
    void addEntityEmptyResult() {
        // given
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        persistenceContext.addEntity(person);

        // when
        var actual = persistenceContext.getEntity(Person.class, 2L);

        // then
        Assertions.assertThat(actual).isEmpty();
    }


    @DisplayName("addEntity 실행시 1건이 정상 저장된다.")
    @Test
    void addEntity() {
        // given
        var person = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);

        // when
        persistenceContext.addEntity(person);

        // then
        var actual = persistenceContext.getEntity(Person.class, 1L).get();
        var expected = person.changeId(1L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("remove 메서드로 객체가 지워질 경우 조회 결과는 0이다.")
    @Test
    void removeEntity() {
        // given
        var person = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        persistenceContext.addEntity(person);

        // when
        var entityBeforeDelete = persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.removeEntity(person);
        var entityAfterDelete = persistenceContext.getEntity(Person.class, 1L);

        // then
        Assertions.assertThat(entityBeforeDelete.get()).isEqualTo(person);
        Assertions.assertThat(entityAfterDelete).isEqualTo(Optional.empty());
    }
}
