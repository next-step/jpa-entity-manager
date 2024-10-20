package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;

class ColumnNameTest {
    @Test
    @DisplayName("@Column 애노테이션이 존재하는 필드로 인스턴스를 생성한다.")
    void constructor_withColumn() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "name");

        // when
        final ColumnName columnName = new ColumnName(field);

        // then
        assertThat(columnName.value()).isEqualTo("nick_name");
    }

    @Test
    @DisplayName("@Column 애노테이션이 존재하지 않는 필드로 인스턴스를 생성한다.")
    void constructor_withoutColumn() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "name");

        // when
        final ColumnName columnName = new ColumnName(field);

        // then
        assertThat(columnName.value()).isEqualTo("nick_name");
    }
}
