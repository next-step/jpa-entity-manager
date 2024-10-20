package persistence.sql.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithoutTable;

import static org.assertj.core.api.Assertions.*;

class TableNameTest {
    @Test
    @DisplayName("@Table 애노테이션이 있는 엔티티로 인스턴스를 생성한다.")
    void constructor_withTable() {
        // when
        final TableName tableName = new TableName(EntityWithId.class);

        // then
        assertThat(tableName.value()).isEqualTo("users");
    }

    @Test
    @DisplayName("@Table 애노테이션이 없는 엔티티로 인스턴스를 생성한다.")
    void constructor_withoutTable() {
        // when
        final TableName tableName = new TableName(EntityWithoutTable.class);

        // then
        assertThat(tableName.value()).isEqualTo("entitywithouttable");
    }
}
