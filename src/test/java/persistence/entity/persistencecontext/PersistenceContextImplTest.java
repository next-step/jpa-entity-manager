package persistence.entity.persistencecontext;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.testfixture.basic.Dog;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.ddl.querybuilder.CreateQueryBuilder;

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
        Person person = new Person(1L,"김철수", 21, "chulsoo.kim@gmail.com", 11);
        Dog dog = new Dog(1L, "바둑이");
        persistenceContext.addEntity(person, 1L);
        persistenceContext.addEntity(dog, 1L);

        // when
        Person actualPerson = persistenceContext.getEntity(Person.class, 1L).get();
        Dog actualDog = persistenceContext.getEntity(Dog.class, 1L).get();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualPerson).isSameAs(person);
            softly.assertThat(actualDog).isSameAs(dog);
        });
    }

    @DisplayName("존재하지 않는 값을 조회시, 리턴 객체는 빈값이다.")
    @Test
    void addEntityEmptyResult() {
        // given
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        persistenceContext.addEntity(person, 1L);

        // when
        Optional<Person> actual = persistenceContext.getEntity(Person.class, 2L);

        // then
        Assertions.assertThat(actual).isEmpty();
    }


    @DisplayName("addEntity 실행시 1건이 정상 저장된다.")
    @Test
    void addEntity() {
        // given
        Person person = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);

        // when
        persistenceContext.addEntity(person, 1L);

        // then
        Person actual = persistenceContext.getEntity(Person.class, 1L).get();
        Person expected = person.changeId(1L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("remove 메서드로 객체가 지워질 경우 조회 결과는 0이다.")
    @Test
    void removeEntity() {
        // given
        Person person = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        persistenceContext.addEntity(person, 1L);

        // when
        Optional<Person> entityBeforeDelete = persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.removeEntity(person);
        Optional<Person> entityAfterDelete = persistenceContext.getEntity(Person.class, 1L);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entityBeforeDelete.get()).isEqualTo(person);
            softly.assertThat(entityAfterDelete).isEqualTo(Optional.empty());
        });
    }

    @DisplayName("스냅샷을 조회시 처음에 저장한 엔티티가 조회된다.")
    @Test
    void getDatabaseSnapshot() {
        // given
        Person person = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        persistenceContext.updateEntity(person, 1L);

        // when
        Person actual = persistenceContext.getDatabaseSnapshot(person, 1L);

        // then
        Assertions.assertThat(actual).isSameAs(person);
    }
}
