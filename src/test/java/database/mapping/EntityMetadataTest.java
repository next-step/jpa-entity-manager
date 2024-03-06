package database.mapping;

import database.sql.dml.Person4;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetadataTest {
    private final EntityMetadata entityMetadata = EntityMetadata.fromClass(Person4.class);

    @Test
    void getTableName() {
        String tableName = entityMetadata.getTableName();
        assertThat(tableName).isEqualTo("users");
    }

    @Test
    void getAllColumnNames() {
        List<String> allColumnNames = entityMetadata.getAllColumnNames();
        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }
}
