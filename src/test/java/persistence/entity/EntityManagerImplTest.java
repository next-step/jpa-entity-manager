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

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class).build());
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(new DropQueryBuilder(Person.class).build());
    }

    @Test
    @DisplayName("데이터베이스에서 Person 조회 테스트")
    void entityManagerFindTest() {
        // given
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);
        jdbcTemplate.execute(new InsertQueryBuilder(new Person("jay", 30, "jay@gmail.com")).build());

        // when
        Person person = entityManager.find(Person.class, 1L);

        // then
        assertAll(
                () -> assertThat(person.getId()).isEqualTo(1L),
                () -> assertThat(person.getName()).isEqualTo("jay"),
                () -> assertThat(person.getAge()).isEqualTo(30),
                () -> assertThat(person.getEmail()).isEqualTo("jay@gmail.com")
        );
    }

    @Test
    @DisplayName("데이터베이스에 Person 저장 테스트")
    void entityManagerPersistTest() {
        // given
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);

        // when
        Person person = entityManager.persist(new Person("jamie", 34, "jaime@gmail.com"));

        // then
        assertThat(person.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("데이터베이스에서 Person 삭제 테스트")
    void entityManagerRemoveTest() {
        // given
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);
        jdbcTemplate.execute(new InsertQueryBuilder(new Person("jay", 30, "jay@gmail.com")).build());

        // when
        entityManager.remove(new Person(1L, "jay", 30, "jay@gmail.com"));

        // then
        assertThatThrownBy(() -> entityManager.find(Person.class, 1L))
                .isInstanceOf(RuntimeException.class);
    }
}
