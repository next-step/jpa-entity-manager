package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityFieldTest {
    @Test
    @DisplayName("인스턴스를 생성한다.")
    void constructor() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "id");

        // when
        final EntityField entityField = new EntityField(field);

        // then
        assertAll(
                () -> assertThat(entityField.getColumnName()).isNotBlank(),
                () -> assertThat(entityField.getColumnLength()).isZero(),
                () -> assertThat(entityField.isId()).isTrue(),
                () -> assertThat(entityField.isGenerationValue()).isTrue(),
                () -> assertThat(entityField.isNotNull()).isFalse()
        );
    }
}
