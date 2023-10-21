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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityPersisterTest {

    private EntityPersister entityPersister;
    private Person fixturePerson;

    @BeforeEach
    void setUp() throws SQLException {
        final Dialect dialect = new H2Dialect();
        entityPersister = new EntityPersister(new MockJdbcTemplate(), new DmlGenerator(dialect));
        fixturePerson = FixturePerson.create(1L);
    }

    @Test
    @DisplayName("renderSelect 을 이용해 entity 의 select 문을 만들 수 있다.")
    void renderSelectTest() {
        final String query = entityPersister.renderSelect(Person.class, 1L);
        assertThat(query).isEqualTo("select id, nick_name, old, email from users where id=1");
    }

    @Test
    @DisplayName("같은 id 의 select 문은 동등한 query 을 반환한다.")
    void renderSelectCachedTest() {
        final String queryV1 = entityPersister.renderSelect(Person.class, 1L);
        final String queryV2 = entityPersister.renderSelect(Person.class, 1L);
        assertThat(queryV1 == queryV2).isTrue();
    }

    @Test
    @DisplayName("renderInsert 를 이용해 entity 의 insert 문을 만들 수 있다.")
    void renderInsertTest() {
        final String query = entityPersister.renderInsert(fixturePerson);
        assertThat(query).isEqualTo("insert into users (nick_name, old, email) values ('min', 30, 'jongmin4943@gmail.com')");
    }

    @Test
    @DisplayName("같은 객체의 insert 문은 동등한 query 를 반환한다.")
    void renderInsertCachedTest() {
        final String queryV1 = entityPersister.renderInsert(fixturePerson);
        final String queryV2 = entityPersister.renderInsert(fixturePerson);
        final String differentQuery = entityPersister.renderInsert(new Person(2L, "test", 10, "test@test.com"));
        assertSoftly(softly -> {
            softly.assertThat(queryV1 == queryV2).isTrue();
            softly.assertThat(queryV1 == differentQuery).isFalse();
        });
    }

    @Test
    @DisplayName("renderUpdate 를 이용해 entity 의 update 문을 만들 수 있다.")
    void renderUpdateTest() {
        final String query = entityPersister.renderUpdate(fixturePerson);
        assertThat(query).isEqualTo("update users set nick_name='min', old=30, email='jongmin4943@gmail.com' where id=1");
    }

    @Test
    @DisplayName("같은 객체의 update 문은 동등한 query 를 반환한다.")
    void renderUpdateCachedTest() {
        final String queryV1 = entityPersister.renderUpdate(fixturePerson);
        final String queryV2 = entityPersister.renderUpdate(fixturePerson);
        final String differentQuery = entityPersister.renderUpdate(new Person(2L, "test", 10, "test@test.com"));
        assertSoftly(softly -> {
            softly.assertThat(queryV1 == queryV2).isTrue();
            softly.assertThat(queryV1 == differentQuery).isFalse();
        });
    }

    @Test
    @DisplayName("renderDelete 을 이용해 entity 의 delete 문을 만들 수 있다.")
    void renderDeleteTest() {
        final String query = entityPersister.renderDelete(fixturePerson);
        assertThat(query).isEqualTo("delete from users where id=1");
    }

    @Test
    @DisplayName("같은 객체의 delete 문은 동등한 query 를 반환한다.")
    void renderDeleteCachedTest() {
        final String queryV1 = entityPersister.renderDelete(fixturePerson);
        final String queryV2 = entityPersister.renderDelete(fixturePerson);
        final String differentQuery = entityPersister.renderDelete(new Person(2L, "test", 10, "test@test.com"));
        assertSoftly(softly -> {
            softly.assertThat(queryV1 == queryV2).isTrue();
            softly.assertThat(queryV1 == differentQuery).isFalse();
        });
    }
}
