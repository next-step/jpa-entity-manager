package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PersistContextImplTest {
    final private PersistenceContext persistContext = new PersistContextImpl();

    @Test
    @DisplayName("엔티티 제거")
    void testGetEntity() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10,  "email", null);
        persistContext.addEntity(id, person);

        persistContext.removeEntity(person);

        assertThat(persistContext.getEntity(id)).isNull();
    }
}
