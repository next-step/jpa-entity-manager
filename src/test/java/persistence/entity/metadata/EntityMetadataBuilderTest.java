package persistence.entity.metadata;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import persistence.entity.Person;

class EntityMetadataBuilderTest {

    @Test
    public void createEntityMetadata() {
        EntityMetadata metadata = EntityMetadataBuilder.build(Person.class);

        assertAll(
                () -> assertEquals("users", metadata.getEntityTable().getTableName()),
                () -> assertEquals("Person", metadata.getEntityTable().getEntityName()),
                () -> assertEquals(4, metadata.getColumns().getColumns().size()),
                () -> assertEquals("id", metadata.getColumns().getIdColumn().getColumnName()),
                () -> assertEquals(3, metadata.getColumns().getInsertTargetColumns().size())
        );
    }

}
