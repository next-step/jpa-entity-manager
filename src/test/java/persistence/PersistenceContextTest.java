package persistence;

import domain.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextTest {

    private DefaultPersistenceContext persistenceContext;

    @Test
    void addEntity() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);

        // when
        persistenceContext.addEntity(id, entity);

        // then
        Object result = persistenceContext.getEntity(id);
        assertThat(result).isEqualTo(entity);
    }
}