package persistence.sql.model;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;

import static org.assertj.core.api.Assertions.assertThat;

class EntityIdInjectorTest {

    @Test
    void Entity_ID_주입() {
        Long id = 1L;
        Person person = new Person();
        Person injectPerson = EntityIdInjector.getInstance().inject(person, id);

        assertThat(injectPerson.getId()).isEqualTo(id);
    }
}
