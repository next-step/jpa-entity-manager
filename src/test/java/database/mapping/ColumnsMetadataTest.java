package database.mapping;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import database.mapping.column.EntityColumn;
import database.sql.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ColumnsMetadataTest {
    private final Dialect dialect = MySQLDialect.INSTANCE;

    private final ColumnsMetadata columnsMetadata = ColumnsMetadata.fromClass(Person.class);

    @Test
    void getAllColumnNames() {
        List<String> allColumnNames = columnsMetadata.getAllColumnNames();
        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }

    @Test
    void getColumnDefinitions() {
        List<String> columnDefinitions = columnsMetadata.getColumnDefinitions(dialect);
        assertThat(columnDefinitions).containsExactly(
                "id BIGINT AUTO_INCREMENT PRIMARY KEY",
                "nick_name VARCHAR(255) NULL",
                "old INT NULL",
                "email VARCHAR(255) NOT NULL"
        );
    }

    @Test
    void getPrimaryKeyColumnName() {
        String primaryKeyColumnName = columnsMetadata.getPrimaryKeyColumnName();
        assertThat(primaryKeyColumnName).isEqualTo("id");
    }

    @Test
    void getGeneralColumnNames() {
        List<String> generalColumnNames = columnsMetadata.getGeneralColumnNames();
        assertThat(generalColumnNames).containsExactly("nick_name", "old", "email");
    }

    @Test
    void getGeneralColumns() {
        List<EntityColumn> columns = columnsMetadata.getGeneralColumns();
        assertAll(
                () -> assertThat(columns).hasSize(3),
                () -> assertThat(columns.get(0).getFieldName()).isEqualTo("name"),
                () -> assertThat(columns.get(1).getFieldName()).isEqualTo("age"),
                () -> assertThat(columns.get(2).getFieldName()).isEqualTo("email")
        );
    }

    @Test
    void getPrimaryKeyValue() {
        Person person = new Person(1020L, "abc", 10, "def@example.com");
        assertThat(columnsMetadata.getPrimaryKeyValue(person)).isEqualTo(1020L);
    }
}
