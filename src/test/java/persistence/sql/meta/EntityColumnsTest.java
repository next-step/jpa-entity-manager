package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithoutID;
import util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityColumnsTest {
    @Test
    @DisplayName("인스턴스를 생성한다.")
    void constructor() {
        // when
        final EntityColumns entityColumns = new EntityColumns(EntityWithId.class);

        // then
        final Field[] fields = EntityWithId.class.getDeclaredFields();
        assertAll(
                () -> assertThat(entityColumns).isNotNull(),
                () -> assertThat(entityColumns.getEntityColumns()).hasSize(4),
                () -> assertThat(entityColumns.getEntityColumns()).containsExactly(
                        new EntityColumn(fields[0]),
                        new EntityColumn(fields[1]),
                        new EntityColumn(fields[2]),
                        new EntityColumn(fields[3])
                )
        );
    }

    @Test
    @DisplayName("id 컬럼을 반환한다.")
    void getIdEntityColumn() {
        // given
        final EntityColumns entityColumns = new EntityColumns(EntityWithId.class);

        // when
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();

        // then
        final EntityColumn expected = new EntityColumn(ReflectionUtils.getField(EntityWithId.class, "id"));
        assertThat(entityColumn).isEqualTo(expected);
    }

    @Test
    @DisplayName("@ID 애노테이션이 없는 엔티티로 id 값을 반환면 예외를 발생한다.")
    void getIdEntityColumn_exception() {
        // given
        final EntityColumns entityColumns = new EntityColumns(EntityWithoutID.class);

        // when & then
        assertThatThrownBy(entityColumns::getIdEntityColumn)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EntityColumns.NOT_ID_FAILED_MESSAGE);
    }
}
