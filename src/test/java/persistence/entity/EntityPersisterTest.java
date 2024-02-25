package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.DmlGenerator;

@DisplayName("EntityPersister class 의")
class EntityPersisterTest {

    private DatabaseServer server;

    private JdbcTemplate jdbcTemplate;
    private DdlGenerator ddlGenerator;
    private EntityPersister entityPersister;
    private DmlGenerator dmlGenerator;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlGenerator = DdlGenerator.from(H2Dialect.getInstance());
        dmlGenerator = DmlGenerator.from();
        entityPersister = EntityPersister.from(jdbcTemplate);
        jdbcTemplate.execute(ddlGenerator.generateCreateQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDropQuery(Person.class));
        server.stop();
    }

    @DisplayName("insert 메서드는")
    @Nested
    class Insert {
        @DisplayName("Entity를 저장한다.")
        @Test
        void insertTest() {
            //given
            Person person = createPerson();

            //when
            entityPersister.insert(person);

            //then
            Person foundPerson = findPerson(1L);

            assertAll(
                () -> assertThat(person).isNotNull(),
                () -> assertThat(person.getName()).isEqualTo(foundPerson.getName()),
                () -> assertThat(person.getAge()).isEqualTo(foundPerson.getAge()),
                () -> assertThat(person.getEmail()).isEqualTo(foundPerson.getEmail())
            );
        }
    }

    @DisplayName("update 메서드는")
    @Nested
    class Update {
        @DisplayName("Entity를 수정한다.")
        @Test
        void updateTest() {
            //given
            Person person = Person.of("user1", 1, "abc@gtest.com", 1);
            entityPersister.insert(person);
            person = jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(Person.class, 1L),
                resultSet -> new EntityRowMapper<>(Person.class).mapRow(resultSet));

            person.updateName("user2");

            //when & then
            assertThat(entityPersister.update(person)).isTrue();
        }
    }

    @DisplayName("delete 메서드는")
    @Nested
    class Delete {
        @DisplayName("Entity를 삭제한다.")
        @Test
        void deleteTest() {
            //given
            Person person = createPerson();
            entityPersister.insert(person);
            person = findPerson(1L);

            //when
            entityPersister.delete(person);

            //then
            assertThatThrownBy(() -> findPerson(1L))
                .isInstanceOf(RuntimeException.class);


        }
    }
    private Person createPerson() {
        return Person.of("user1", 1, "abc@gtest.com", 1);
    }

    private Person findPerson(long id) {
        return jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(Person.class, id),
            resultSet -> new EntityRowMapper<>(Person.class).mapRow(resultSet));
    }
}
