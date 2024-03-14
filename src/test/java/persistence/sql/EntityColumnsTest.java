package persistence.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.entity.Person;

class EntityColumnsTest {

    @Test
    void getPrimaryColumn() {
        // given
        EntityColumns entityColumns = new EntityColumns(Person.class);

        // when
        EntityColumn primaryColumn = entityColumns.getPrimaryColumn();

        // then
        assertAll(
            () -> assertNotNull(primaryColumn),
            () -> assertEquals("id", primaryColumn.getColumnName()),
            () -> assertTrue(primaryColumn.isPrimary())
        );
    }

    @Test
    void getColumnsWithoutPrimary() {
        // given
        EntityColumns entityColumns = new EntityColumns(Person.class);

        // when
        List<EntityColumn> columnsWithoutPrimaryKey = entityColumns.getColumnsWithoutPrimary();

        Stream<String> columnNamesWithoutPrimaryKey = columnsWithoutPrimaryKey.stream()
            .map(EntityColumn::getColumnName);

        // then
        assertAll(
            () -> assertNotNull(columnsWithoutPrimaryKey),
            () -> assertThat(columnsWithoutPrimaryKey).allMatch(entityColumn -> !entityColumn.isPrimary()),
            () -> assertEquals(3, columnsWithoutPrimaryKey.size()),
            () -> assertThat(columnNamesWithoutPrimaryKey).containsExactly("nick_name", "old", "email")
        );
    }
}
