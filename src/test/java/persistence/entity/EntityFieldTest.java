package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.Person;

import static fixture.PersonFixtures.createPerson;
import static org.assertj.core.api.Assertions.assertThat;

class EntityFieldTest {

    @Test
    @DisplayName("필드 값을 설정한다")
    void setFieldValue() throws NoSuchFieldException {
        // given
        Person person = createPerson();
        EntityField field = new EntityField(person.getClass().getDeclaredField("id"));

        // when
        field.setValue(person, 1111L);

        // then
        assertThat(person.getId()).isEqualTo(1111L);
    }

    @Test
    @DisplayName("필드 값을 조회한다")
    void getFieldValue() throws NoSuchFieldException {
        // given
        Person person = createPerson();
        EntityField field = new EntityField(person.getClass().getDeclaredField("name"));

        // when
        Object result = field.getValue(person);

        // then
        assertThat(result).isEqualTo(person.getName());
    }
}