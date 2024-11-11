package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static persistence.query.QueryExecutor.create;
import static persistence.query.QueryExecutor.drop;
import static persistence.query.QueryExecutor.insert;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.impl.DefaultEntityManager;
import persistence.fixture.SimpleEntityFixture;
import sample.domain.Person;

class DefaultEntityManagerTest {

    private DatabaseServer server;

    @BeforeEach
    void beforeEach() throws SQLException {
        server = new H2();
        server.start();
        create(Person.class, new JdbcTemplate(server.getConnection()));
        create(SimpleEntityFixture.class, new JdbcTemplate(server.getConnection()));
    }

    @AfterEach
    void afterEach() throws SQLException {
        drop(Person.class, new JdbcTemplate(server.getConnection()));
        drop(SimpleEntityFixture.class, new JdbcTemplate(server.getConnection()));
        server.stop();
    }

    @Test
    @DisplayName("[성공] Person Entity 조회")
    void find() throws SQLException {
        Person person = new Person("hellonayeon", 20, "hellonayeon@abc.com");
        insert(person, new JdbcTemplate(server.getConnection()));

        EntityManager entityManager = new DefaultEntityManager(server.getConnection());
        Person findPerson = entityManager.find(Person.class, 1L);

        assertAll("조회한 Entity 필드값 검증",
                () -> assertThat(findPerson.getName()).isEqualTo("hellonayeon"),
                () -> assertThat(findPerson.getAge()).isEqualTo(20),
                () -> assertThat(findPerson.getEmail()).isEqualTo("hellonayeon@abc.com")
        );
    }

    @Test
    @DisplayName("[성공] Person Entity 저장")
    void persist() throws SQLException {
        Person person = new Person("hellonayeon", 20, "hellonayeon@abc.com");
        EntityManager entityManager = new DefaultEntityManager(server.getConnection());
        assertDoesNotThrow(() -> entityManager.persist(person));
    }

    @Test
    @DisplayName("[성공] Person Entity 삭제")
    void remove() throws SQLException {
        EntityManager entityManager = new DefaultEntityManager(server.getConnection());
        Person person = new Person("hellonayeon", 20, "hellonayeon@abc.com");
        entityManager.persist(person);

        assertDoesNotThrow(() -> entityManager.remove(person));
    }

    @Test
    @DisplayName("[성공] Entity 업데이트")
    void merge() throws SQLException {
        SimpleEntityFixture entity = new SimpleEntityFixture("hellonayeon", 0);
        insert(entity, new JdbcTemplate(server.getConnection()));

        Long ID = 1L;
        EntityManager entityManager = new DefaultEntityManager(server.getConnection());
        SimpleEntityFixture findEntity = entityManager.find(SimpleEntityFixture.class, ID);
        findEntity.setName("Nayeon Kwon");

        entityManager.merge(findEntity);

        SimpleEntityFixture entityAfterMerge = entityManager.find(SimpleEntityFixture.class, ID);
        assertAll("merge() 후 Entity 필드값 검증",
                () -> assertEquals(entityAfterMerge.getName(), "Nayeon Kwon"),
                () -> assertEquals(entityAfterMerge.getNumber(), 0));
    }

}
