package persistence.entity;

import domain.FixturePerson;
import domain.Person;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIterable;

class EntityPersisterTest {

    private EntityPersister entityPersister;
    private Person fixturePerson;

    @BeforeEach
    void setUp() throws SQLException {
        entityPersister = new EntityPersister(Person.class, new MockDmlGenerator(), new MockJdbcTemplate());
        fixturePerson = FixturePerson.create(1L);
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

    @Test
    @DisplayName("getColumnNames 을 이용해 entity 의 column 이름들을 반환 받을 수 있다.")
    void getColumnNamesTest() {
        final List<String> columnNames = entityPersister.getColumnNames();

        assertThatIterable(columnNames).containsExactly("id", "nick_name", "old", "email");
    }

    @Test
    @DisplayName("getColumnFieldNames 을 이용해 entity 의 필드 이름들을 반환 받을 수 있다.")
    void getColumnFieldNamesTest() {
        final List<String> columnFieldNames = entityPersister.getColumnFieldNames();

        assertThatIterable(columnFieldNames).containsExactly("id", "name", "age", "email");
    }

    @Test
    @DisplayName("getColumnSize 을 이용해 entity 의 필드 갯수를 반환 받을 수 있다.")
    void getColumnSizeTest() {
        final int size = entityPersister.getColumnSize();

        assertThat(size).isEqualTo(4);
    }

    @Test
    @DisplayName("getIdColumnFieldName 을 이용해 entity 의 id field 이름을 반환 받을 수 있다.")
    void getIdColumnFieldNameTest() {
        String idColumnFieldName = entityPersister.getIdColumnFieldName();

        assertThat(idColumnFieldName).isEqualTo("id");
    }

}
