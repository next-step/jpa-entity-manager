package database.sql.util;

import database.sql.ddl.OldPerson1;
import database.sql.dml.Person4;
import database.sql.util.type.MySQLTypeConverter;
import database.sql.util.type.TypeConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnMetadataTest {
    private final TypeConverter typeConverter = new MySQLTypeConverter();
    private final ColumnsMetadata columnsMetadata = new ColumnsMetadata(Person4.class);

    @Test
    void getTableNameWithoutTableAnnotation() {
        EntityMetadata entityMetadata = new EntityMetadata(OldPerson1.class);
        String tableName = entityMetadata.getTableName();
        assertThat(tableName).isEqualTo("OldPerson1");
    }

    @Test
    void getAllColumnNames() {
        List<String> allColumnNames = columnsMetadata.getAllColumnNames();
        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }

    @Test
    void getColumnDefinitions() {
        List<String> res = columnsMetadata.getColumnDefinitions(typeConverter);
        assertThat(res).containsExactly(
                "id BIGINT AUTO_INCREMENT PRIMARY KEY",
                "nick_name VARCHAR(255) NULL",
                "old INT NULL",
                "email VARCHAR(255) NOT NULL");
    }

    @Test
    void getPrimaryKeyColumnName() {
        String columnName = columnsMetadata.getPrimaryKeyColumnName();
        assertThat(columnName).isEqualTo("id");
    }

    @Test
    void getGeneralColumnNames() {
        List<String> generalColumnNames = columnsMetadata.getGeneralColumnNames();
        assertThat(generalColumnNames).containsExactly("nick_name", "old", "email");
    }
}
