package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderTest extends DatabaseTestBase {

    @Test
    @DisplayName("findById() 메서드 테스트")
    void findById() {
        Person person = entityLoader.findById(Person.class, 1L);

        assertThat(person.getId()).isEqualTo(1L);
    }

}
