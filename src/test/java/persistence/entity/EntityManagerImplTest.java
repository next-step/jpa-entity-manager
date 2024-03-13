package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.entity.Person;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class).build());
        entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(new DropQueryBuilder(Person.class).build());
    }

    @Test
    @DisplayName("EntityManager 이용한 Person 조회 테스트")
    void entityManagerFindTest() {
        // given
        Person expectedPerson = new Person("jay", 30, "jay@gmail.com");
        entityManager.persist(expectedPerson);

        // when
        Person person = entityManager.find(Person.class, expectedPerson.getId());

        // then
        assertAll(
                () -> assertThat(person.getId()).isEqualTo(expectedPerson.getId()),
                () -> assertThat(person.getName()).isEqualTo(expectedPerson.getName()),
                () -> assertThat(person.getAge()).isEqualTo(expectedPerson.getAge()),
                () -> assertThat(person.getEmail()).isEqualTo(expectedPerson.getEmail())
        );
    }

    @Test
    @DisplayName("EntityManager 이용한 Person 저장 테스트")
    void entityManagerPersistTest() {
        // when
        Person person = entityManager.persist(new Person("jamie", 34, "jaime@gmail.com"));

        // then
        assertThat(person.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("EntityManager 이용한 Person 삭제 테스트")
    void entityManagerRemoveTest() {
        // given
        Person person = new Person("jay", 30, "jay@gmail.com");
        entityManager.persist(person);

        // when
        entityManager.remove(person);

        // then
        assertThatThrownBy(() -> entityManager.find(Person.class, person.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("EntityManager 이용한 Person 수정 테스트")
    void entityManagerMergeTest() {
        // given
        entityManager.persist(new Person("jay", 30, "jay@gmail.com"));
        Person person = entityManager.find(Person.class, 1L);
        person.updateAge(34);

        // when
        entityManager.merge(person);

        // then
        assertThat(entityManager.find(Person.class, 1L).getAge()).isEqualTo(34);
    }

}
