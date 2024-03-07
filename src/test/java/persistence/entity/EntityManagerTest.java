package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.notcolumn.Person;
import persistence.sql.common.DtoMapper;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;
import java.util.stream.Stream;

import static persistence.entity.TestFixture.person_철수;
import static persistence.entity.TestFixture.person_철수_id있음;
import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;
import static persistence.sql.dml.TestFixture.person_영희;
import static persistence.sql.dml.TestFixture.person_짱구;

class EntityManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerTest.class);
    DatabaseServer server;
    private JdbcTemplate jdbcTemplate;

    EntityManager<Person> entityManager;

    @BeforeEach
    void setUp() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
            entityManager = new EntityManagerImpl<>(jdbcTemplate);

            jdbcTemplate.execute(new CreateQueryBuilder(Person.class).getQuery());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(DROP_TABLE_USERS);
        server.stop();
    }

    @Test
    void find() {
        // given
        jdbcTemplate.execute(new InsertQueryBuilder(Person.class).getInsertQuery(person_철수));

        // when
        Person actual = entityManager.find(Person.class, 1L);

        // then
        Assertions.assertThat(actual.getName()).isEqualTo(person_철수.getName());
        Assertions.assertThat(actual.getAge()).isEqualTo(person_철수.getAge());
        Assertions.assertThat(actual.getEmail()).isEqualTo(person_철수.getEmail());
    }

    @Test
    void persist() {
        // given & when
        Person actual = (Person) entityManager.persist(person_철수);

        // then
        Assertions.assertThat(actual.getName()).isEqualTo(person_철수.getName());
        Assertions.assertThat(actual.getAge()).isEqualTo(person_철수.getAge());
        Assertions.assertThat(actual.getEmail()).isEqualTo(person_철수.getEmail());
    }

    @Test
    void remove() {
        // given
        List<String> insertQueries = Stream.of(persistence.sql.dml.TestFixture.person_철수, person_영희, person_짱구)
                .map(person -> new InsertQueryBuilder(Person.class).getInsertQuery(person)).toList();

        for (String query : insertQueries) {
            jdbcTemplate.execute(query);
        }

        // when
        entityManager.remove(person_철수_id있음);

        // then
        String query = new SelectQueryBuilder(Person.class).getFindAllQuery();
        List<Person> actual = jdbcTemplate.query(query, new DtoMapper<>(Person.class));
        Assertions.assertThat(actual.get(0).getName()).isEqualTo(person_영희.getName());
        Assertions.assertThat(actual.get(0).getAge()).isEqualTo(person_영희.getAge());
        Assertions.assertThat(actual.get(0).getEmail()).isEqualTo(person_영희.getEmail());
        Assertions.assertThat(actual.get(1).getName()).isEqualTo(person_짱구.getName());
        Assertions.assertThat(actual.get(1).getAge()).isEqualTo(person_짱구.getAge());
        Assertions.assertThat(actual.get(1).getEmail()).isEqualTo(person_짱구.getEmail());

    }

    @Test
    void update() {
        // given
        jdbcTemplate.execute(new InsertQueryBuilder(Person.class).getInsertQuery(person_철수));

        // when
        Assertions.assertThat(entityManager.update(person_영희)).isEqualTo(true);

        List<Person> result = jdbcTemplate.query(new SelectQueryBuilder(Person.class).getFindAllQuery(), new DtoMapper<>(Person.class));

        // then
        Assertions.assertThat(result.size()).isEqualTo(1L);
        Assertions.assertThat(result.get(0).getName()).isEqualTo(person_영희.getName());
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(person_영희.getAge());
        Assertions.assertThat(result.get(0).getEmail()).isEqualTo(person_영희.getEmail());

    }
}
