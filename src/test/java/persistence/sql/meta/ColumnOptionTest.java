package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class ColumnOptionTest {
    @Test
    @DisplayName("@Column 애노테이션의 nullable 속성이 true인 필드로 인스턴스를 생성한다.")
    void constructor_withNullableTrue() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "name");

        // when
        final ColumnOption columnOption = new ColumnOption(field);

        // then
        assertThat(columnOption.isNotNull()).isFalse();
    }

    @Test
    @DisplayName("@Column 애노테이션의 nullable 속성이 false인 필드로 인스턴스를 생성한다.")
    void constructor_withNullableFalse() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "email");

        // when
        final ColumnOption columnOption = new ColumnOption(field);

        // then
        assertThat(columnOption.isNotNull()).isTrue();
    }
}
