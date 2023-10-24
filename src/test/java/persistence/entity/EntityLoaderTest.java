package persistence.entity;

import domain.Person;
import mock.MockDmlGenerator;
import mock.MockEntityPersister;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderTest {

    @Test
    @DisplayName("renderSelect 을 이용해 entity 의 select 문을 만들 수 있다.")
    void renderSelectTest() throws SQLException {
        final Class<Person> clazz = Person.class;
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockEntityPersister(clazz), new MockJdbcTemplate(), new MockDmlGenerator());
        final String query = entityLoader.renderSelect(1L);
        assertThat(query).isEqualTo("select id, nick_name, old, email from users where id=1");
    }
}
