package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.Person;

import java.sql.SQLException;

import static fixture.PersonFixtures.createPerson;
import static fixture.TableFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class EntityManagerTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityManager = new BasicEntityManger(jdbcTemplate);

        createTable(Person.class, jdbcTemplate);
    }

    @AfterEach
    void clear() {
        dropTable(Person.class, jdbcTemplate);
    }

    @Test
    @DisplayName("엔티티를 조회한다")
    void find() {
        // given
        insert(createPerson(), jdbcTemplate);

        // when
        Person result = entityManager.find(Person.class, 1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("엔티티를 저장한다")
    void persist() {
        // given
        Person person = createPerson();

        // when
        entityManager.persist(person);

        // then
        Person result = select(Person.class, 1L, jdbcTemplate);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("엔티티를 삭제한다")
    void remove() {
        // given
        insert(createPerson(), jdbcTemplate);
        Person person = select(Person.class, 1L, jdbcTemplate);

        // when
        entityManager.remove(person);

        // then
        Person result = select(Person.class, 1L, jdbcTemplate);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("1차 캐싱을 확인한다")
    void firstLevelCache() {
        // given
        Person person = createPerson();
        entityManager.persist(person);

        // when
        Person result = entityManager.find(Person.class, 1L);

        // then
        assertThat(result == person).isTrue();
    }

    @Test
    @DisplayName("원본 엔티티와 비교하여 변경사항이 있을 경우 엔티티를 업데이트한다")
    void merge() {
        // given
        Person person = createPerson();
        entityManager.persist(person);

        // when
        person.changeName("lee");
        entityManager.merge(person);

        // then
        Person selectedPerson = entityManager.find(Person.class, person.getId());
        assertThat(selectedPerson.getName()).isEqualTo("lee");
    }
}