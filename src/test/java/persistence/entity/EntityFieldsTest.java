package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

class EntityFieldsTest {

    @Test
    @DisplayName("id 필드를 조회한다")
    void getId() throws NoSuchFieldException {
        // given
        EntityFields entityFields = EntityFields.of(Person.class);

        // when
        EntityField result = entityFields.getId();

        // then
        assertThat(result).isEqualTo(new EntityField(Person.class.getDeclaredField("id")));
    }
}