package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityColumnTest {
    @Test
    @DisplayName("인스턴스를 생성한다.")
    void constructor() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "id");

        // when
        final EntityColumn entityColumn = new EntityColumn(field);

        // then
        assertAll(
                () -> assertThat(entityColumn.getColumnName()).isNotBlank(),
                () -> assertThat(entityColumn.getColumnLength()).isZero(),
                () -> assertThat(entityColumn.isId()).isTrue(),
                () -> assertThat(entityColumn.isGenerationValue()).isTrue(),
                () -> assertThat(entityColumn.isNotNull()).isFalse()
        );
    }
}
