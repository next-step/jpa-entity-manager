package persistence.entity;

import domain.Person;
import mock.MockDatabaseServer;
import mock.MockEntityPersister;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.PersistenceEnvironment;
import persistence.dialect.h2.H2Dialect;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderTest {

    @Test
    @DisplayName("renderSelect 을 이용해 entity 의 select 문을 만들 수 있다.")
    void renderSelectTest() {
        final Class<Person> clazz = Person.class;
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new PersistenceEnvironment(new MockDatabaseServer(), new H2Dialect()), new MockEntityPersister(clazz));
        final String query = entityLoader.renderSelect(1L);
        assertThat(query).isEqualTo("select id, nick_name, old, email from users where id=1");
    }
}
