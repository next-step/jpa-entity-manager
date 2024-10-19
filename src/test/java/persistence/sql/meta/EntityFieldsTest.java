package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import util.ReflectionUtils;

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
                        new EntityColumn(fields[0]),
                        new EntityColumn(fields[1]),
                        new EntityColumn(fields[2]),
                        new EntityColumn(fields[3])
                )
        );
    }

    @Test
    @DisplayName("id 컬럼을 반환한다.")
    void getIdEntityField() {
        // given
        final EntityFields entityFields = new EntityFields(EntityWithId.class);

        // when
        final EntityColumn entityColumn = entityFields.getIdEntityField();

        // then
        final EntityColumn expected = new EntityColumn(ReflectionUtils.getField(EntityWithId.class, "id"));
        assertThat(entityColumn).isEqualTo(expected);
    }
}
