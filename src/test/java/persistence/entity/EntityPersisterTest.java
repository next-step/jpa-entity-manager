package persistence.entity;

import database.MockJdbcTemplate;
import domain.FixturePerson;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.h2.H2Dialect;
import persistence.sql.dml.DmlGenerator;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest {

    private EntityPersister entityPersister;
    private Person fixturePerson;

    @BeforeEach
    void setUp() throws SQLException {
        final Dialect dialect = new H2Dialect();
        entityPersister = new EntityPersister(Person.class, new MockJdbcTemplate(), new DmlGenerator(dialect));
        fixturePerson = FixturePerson.create(1L);
    }

    @Test
    @DisplayName("renderSelect 을 이용해 entity 의 select 문을 만들 수 있다.")
    void renderSelectTest() {
        final String query = entityPersister.renderSelect(1L);
        assertThat(query).isEqualTo("select id, nick_name, old, email from users where id=1");
    }

    @Test
    @DisplayName("renderInsert 를 이용해 entity 의 insert 문을 만들 수 있다.")
    void renderInsertTest() {
        final String query = entityPersister.renderInsert(fixturePerson);
        assertThat(query).isEqualTo("insert into users (nick_name, old, email) values ('min', 30, 'jongmin4943@gmail.com')");
    }

    @Test
    @DisplayName("renderUpdate 를 이용해 entity 의 update 문을 만들 수 있다.")
    void renderUpdateTest() {
        final String query = entityPersister.renderUpdate(fixturePerson);
        assertThat(query).isEqualTo("update users set nick_name='min', old=30, email='jongmin4943@gmail.com' where id=1");
    }


    @Test
    @DisplayName("renderDelete 을 이용해 entity 의 delete 문을 만들 수 있다.")
    void renderDeleteTest() {
        final String query = entityPersister.renderDelete(fixturePerson);
        assertThat(query).isEqualTo("delete from users where id=1");
    }

}
