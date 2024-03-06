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
import persistence.sql.dml.entity.Person;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityLoaderImplTest {

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
    @DisplayName("EntityLoaderImpl 객체를 이용한 엔티티 조회 테스트")
    void entityLoaderFindTest() {
        // given
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);
        EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate);
        entityManager.persist(new Person("jay", 32, "jay@gmail.com"));

        // when
        Person person = entityLoader.find(Person.class, 1L);

        // then
        assertAll(
                () -> assertThat(person.getId()).isEqualTo(1L),
                () -> assertThat(person.getName()).isEqualTo("jay"),
                () -> assertThat(person.getAge()).isEqualTo(32),
                () -> assertThat(person.getEmail()).isEqualTo("jay@gmail.com")
        );
    }

}
