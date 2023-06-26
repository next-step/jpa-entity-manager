package persistence.common;

import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.Person;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FieldsTest {

    @Test
    @DisplayName("필드를 조회한다")
    void getField() throws NoSuchFieldException {
        // given
        Fields fields = Fields.of(Person.class);

        // when
        Field result = fields.getField(Id.class);

        // then
        assertThat(result).isEqualTo(Person.class.getDeclaredField("id"));
    }

    @Test
    @DisplayName("접근 가능한 필드를 조회한다")
    void getAccessField() throws NoSuchFieldException {
        // given
        Fields fields = Fields.of(Person.class);

        // when
        AccessibleField result = fields.getAccessibleField(Id.class);

        // then
        assertThat(result).isEqualTo(new AccessibleField(Person.class.getDeclaredField("id")));
    }

    @Test
    @DisplayName("제외되어야 하는 어노테이션이 주어졌을 때, 해당 어노테이션이 설정되지 않은 필드 목록을 반환한다")
    void getFields() throws NoSuchFieldException {
        // given
        Fields fields = Fields.of(Person.class);

        // when
        List<Field> result = fields.getFields(Id.class);

        // then
        assertThat(result.contains(Person.class.getDeclaredField("id"))).isFalse();
    }
}