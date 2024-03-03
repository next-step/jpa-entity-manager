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
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityPersisterImplTest {

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
    @DisplayName("EntityPersisterImpl 객체를 이용한 엔티티 업데이트 테스트")
    void entityPersisterUpdateTest() {
        // given
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);
        EntityPersister entityPersister = new EntityPersisterImpl(jdbcTemplate);
        jdbcTemplate.execute(new InsertQueryBuilder(new Person("jay", 32, "jamie@gmail.com")).build());
        String updateName = "jamie";
        int updateAge = 34;

        // when
        entityPersister.update(new Person(1L, updateName, updateAge, null));
        Person person = entityManager.find(Person.class, 1L);

        // then
        assertAll(
                () -> assertThat(person.getName()).isEqualTo(updateName),
                () -> assertThat(person.getAge()).isEqualTo(updateAge)
        );
    }

}