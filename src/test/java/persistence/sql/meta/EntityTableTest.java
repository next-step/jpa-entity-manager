package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithoutID;
import persistence.fixture.EntityWithoutTable;
import persistence.fixture.NotEntity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityTableTest {
    @Test
    @DisplayName("@Entity 애노테이션이 존재하는 클래스로 인스턴스를 생성한다.")
    void constructor() {
        // when
        final EntityTable entityTable = new EntityTable(EntityWithId.class);

        // then
        assertThat(entityTable).isNotNull();
    }

    @Test
    @DisplayName("@Entity 애노테이션이 존재하지 않는 클래스로 인스턴스를 생성하면 예외를 발생한다.")
    void constructor_exception() {
        // when & then
        assertThatThrownBy(() -> new EntityTable(NotEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EntityTable.NOT_ENTITY_FAILED_MESSAGE);
    }

    @Test
    @DisplayName("where절을 반환한다.")
    void getWhereClause() {
        // given
        final EntityTable entityTable = new EntityTable(EntityWithId.class);

        // when
        final String whereClause = entityTable.getWhereClause(1);

        // then
        assertThat(whereClause).isEqualTo("id = 1");
    }

    @Test
    @DisplayName("@ID 애노테이션이 없는 엔티티로 where절을 반환면 예외를 발생한다.")
    void getWhereClause_exception() {
        // given
        final EntityTable entityTable = new EntityTable(EntityWithoutID.class);

        // when & then
        assertThatThrownBy(() -> entityTable.getWhereClause(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EntityTable.NOT_ID_FAILED_MESSAGE);
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
