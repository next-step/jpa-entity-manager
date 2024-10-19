package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithoutID;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityFieldsTest {
    @Test
    @DisplayName("인스턴스를 생성한다.")
    void constructor() {
        // when
        final EntityFields entityFields = new EntityFields(EntityWithId.class);

        // then
        final Field[] fields = EntityWithId.class.getDeclaredFields();
        assertAll(
                () -> assertThat(entityFields).isNotNull(),
                () -> assertThat(entityFields.getEntityFields()).hasSize(4),
                () -> assertThat(entityFields.getEntityFields()).containsExactly(
                        new EntityField(fields[0]),
                        new EntityField(fields[1]),
                        new EntityField(fields[2]),
                        new EntityField(fields[3])
                )
        );
    }

    @Test
    @DisplayName("id 값을 반환한다.")
    void getIdValue() {
        // given
        final EntityTable entityTable = new EntityTable(EntityWithId.class);
        final EntityWithId entityWithId = new EntityWithId(1L, "Jaden", 30, "test@email.com");

        // when
        final Object idValue = entityTable.getIdValue(entityWithId);

        // then
        assertThat(idValue).isEqualTo("1");
    }

    @Test
    @DisplayName("@ID 애노테이션이 없는 엔티티로 id 값을 반환면 예외를 발생한다.")
    void getIdValue_exception() {
        // given
        final EntityTable entityTable = new EntityTable(EntityWithoutID.class);
        final EntityWithId entityWithId = new EntityWithId(1L, "Jaden", 30, "test@email.com");

        // when & then
        assertThatThrownBy(() -> entityTable.getIdValue(entityWithId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EntityTable.NOT_ID_FAILED_MESSAGE);
    }
}
