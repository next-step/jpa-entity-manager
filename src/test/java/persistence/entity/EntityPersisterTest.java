package persistence.entity;

import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.notcolumn.Person;
import persistence.sql.ddl.CreateQueryBuilder;

class EntityPersisterTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityPersisterTest.class);
    EntityPersister entityPersister;
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        try {
            var server = new H2();
            server.start();
            var jdbcTemplate = new JdbcTemplate(server.getConnection());

            entityManager = new EntityManagerImpl<>(jdbcTemplate);
            entityPersister = new EntityPersister(jdbcTemplate);

            jdbcTemplate.execute(new CreateQueryBuilder(Person.class).getQuery());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
    @Test
    @DisplayName("insert 시 Person 1건 저장된다.")
    void persistTest() {
        // given
        var testFixture = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);

        // when
        entityPersister.insert(testFixture);

        // then
        Person actual = entityManager.find(Person.class, 1L);
        Person expected = new Person(1L, "김철수", 21, "chulsoo.kim@gmail.com", 11);
        Assertions.assertEquals(expected, actual);
    }

}
