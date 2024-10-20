package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;

class ColumnLengthTest {
    @Test
    @DisplayName("@Column 애노테이션의 length 속성이 존재하는 String 필드로 인스턴스를 생성한다.")
    void constructor_withLength() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "name");

        // when
        final ColumnLength columnLength = new ColumnLength(field);

        // then
        assertThat(columnLength.value()).isEqualTo(20);
    }

    @Test
    @DisplayName("@Column 애노테이션의 length 속성이 존재하지 않는 String 필드로 인스턴스를 생성한다.")
    void constructor_withoutLength() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "email");

        // when
        final ColumnLength columnLength = new ColumnLength(field);

        // then
        assertThat(columnLength.value()).isEqualTo(255);
    }

    @Test
    @DisplayName("@Column 애노테이션의 length 속성이 존재하지 않는 Strign 이외 필드로 인스턴스를 생성한다.")
    void constructor_notString() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "age");

        // when
        final ColumnLength columnLength = new ColumnLength(field);

        // then
        assertThat(columnLength.value()).isEqualTo(0);
    }
}
