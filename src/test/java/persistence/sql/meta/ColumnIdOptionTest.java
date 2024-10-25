package persistence.sql.meta;

import jakarta.persistence.GenerationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithOnlyId;
import persistence.fixture.EntityWithoutID;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ColumnIdOptionTest {
    @Test
    @DisplayName("@Id, @GeneratedValue 애노테이션이 존재하는 필드로 인스턴스를 생성한다.")
    void constructor_withIdAndGeneratedValue() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithId.class, "id");

        // when
        final ColumnIdOption columnIdOption = new ColumnIdOption(field);

        // then
        assertAll(
                () -> assertThat(columnIdOption.isId()).isTrue(),
                () -> assertThat(columnIdOption.getGenerationType()).isEqualTo(GenerationType.IDENTITY),
                () -> assertThat(columnIdOption.isGenerationValue()).isTrue()
        );
    }

    @Test
    @DisplayName("@Id 애노테이션만 존재하는 필드로 인스턴스를 생성한다.")
    void constructor_withId() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithOnlyId.class, "id");

        // when
        final ColumnIdOption columnIdOption = new ColumnIdOption(field);

        // then
        assertAll(
                () -> assertThat(columnIdOption.isId()).isTrue(),
                () -> assertThat(columnIdOption.getGenerationType()).isNull(),
                () -> assertThat(columnIdOption.isGenerationValue()).isFalse()
        );
    }

    @Test
    @DisplayName("@GeneratedValue 애노테이션만 존재하는 필드로 인스턴스를 생성한다.")
    void constructor_withGeneratedValue() {
        // given
        final Field field = ReflectionUtils.getField(EntityWithoutID.class, "id");

        // when
        final ColumnIdOption columnIdOption = new ColumnIdOption(field);

        // then
        assertAll(
                () -> assertThat(columnIdOption.isId()).isFalse(),
                () -> assertThat(columnIdOption.isGenerationValue()).isFalse()
        );
    }
}
