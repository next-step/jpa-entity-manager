package persistence.repository;

import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.BasicEntityManger;
import persistence.model.Person;

import java.sql.SQLException;

import static fixture.PersonFixtures.createPerson;
import static fixture.TableFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CustomJpaRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private CustomJpaRepository<Person, Long> repository;

    @BeforeEach
    void setUp() throws SQLException {
        jdbcTemplate = new JdbcTemplate(new H2().getConnection());
        repository = new CustomJpaRepository<>(new BasicEntityManger(jdbcTemplate));

        createTable(Person.class, jdbcTemplate);
    }

    @AfterEach
    void clear() {
        dropTable(Person.class, jdbcTemplate);
    }

    @Test
    @DisplayName("새로운 엔티티를 저장한다")
    void saveNew() {
        // given
        Person person = createPerson();

        // when
        Person savedPerson = repository.save(person);

        // then
        Person selectedPerson = select(Person.class, 1L, jdbcTemplate);
        assertAll(
                () -> assertThat(person.getId()).isEqualTo(1L),
                () -> assertThat(savedPerson.getId()).isEqualTo(1L),
                () -> assertThat(selectedPerson).isEqualTo(savedPerson)
        );
    }

    @Test
    @DisplayName("기존에 존재하는 엔티티를 업데이트한다")
    void update() {
        // given
        Person savedPerson = repository.save(createPerson());

        // when
        savedPerson.changeName("lee");
        Person updatedPerson = repository.save(savedPerson);

        // then
        Person selectedPerson = select(Person.class, 1L, jdbcTemplate);
        assertThat(selectedPerson.getName()).isEqualTo("lee");
    }
}