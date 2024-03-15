package persistence.entity.entitymanager;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.loader.EntityLoader;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerImpl;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.entity.persister.EntityPersister;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.common.DtoMapper;
import persistence.sql.ddl.querybuilder.CreateQueryBuilder;
import persistence.sql.dml.querybuilder.SelectQueryBuilder;

import java.util.List;
import java.util.Optional;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class EntityManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private EntityManager entityManager;

    private PersistenceContext persistenceContext;
    private EntityPersister entityPersister;
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

        persistenceContext = new PersistenceContextImpl();
        entityPersister = new EntityPersister(jdbcTemplate);
        entityLoader = new EntityLoader(jdbcTemplate);
        entityManager = new EntityManagerImpl(persistenceContext, entityLoader, entityPersister);
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
    void find_entityCache에서_값을_가져온다() {
        // given
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityManager.persist(person);

        // when
        Person actual = entityManager.find(Person.class, 1L).get();

        // then
        Optional<Person> expected = persistenceContext.getEntity(Person.class, person.getId());
        Assertions.assertThat(actual).isSameAs(expected.get());
    }

    @Test
    void find_entityCache와_entityLoader에_값이_없을경우_OptionalEmpty를_반환한다() {
        // given&when
        Optional<Person> actual = entityManager.find(Person.class, 1L);

        // then
        Assertions.assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    void find_entityCache에_값이_없고_entity_loader에_존재하면_entityloader를_통해_값을_가져오고_entityCache에도_값을_넣는다() {
        // given&when
        entityPersister.insert(new Person("김철수", 21, "chulsoo.kim@gmail.com", 11));
        Optional<Person> actual = entityManager.find(Person.class, 1L);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual).isSameAs(entityLoader.find(Person.class, 1L));
        softAssertions.assertThat(actual).isSameAs(persistenceContext.getEntity(Person.class, 1L));
    }

    @Test
    void merge시_entity가_업데이트된다() {
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        entityManager.persist(person);

        Person changedPerson = person.changeEmail("soo@gmail.com");

        entityManager.merge(changedPerson);

        Assertions.assertThat(entityManager.find(person.getClass(), person.getId()).get()).isEqualTo(changedPerson);
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
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.get(0)).isEqualTo(new Person(2L, "김영희", 15, "younghee.kim@gmail.com", 11));
        softAssertions.assertThat(actual.get(1)).isEqualTo(new Person(3L, "신짱구", 15, "jjangoo.sin@gmail.com", 11));
    }
}
