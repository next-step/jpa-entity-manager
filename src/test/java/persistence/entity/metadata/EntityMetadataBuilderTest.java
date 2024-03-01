package persistence.entity.metadata;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import persistence.entity.Person;

class EntityMetadataBuilderTest {

    @Test
    public void createEntityMetadata() {
        EntityMetadata metadata = EntityMetadataBuilder.build(Person.class);

        assertAll(
                () -> assertEquals("users", metadata.getTableName()),
                () -> assertEquals(4, metadata.getColumns().size()),
                () -> assertEquals("id", metadata.getIdColumn().getName()),
                () -> assertEquals(3, metadata.getInsertTargetColumns().size())
        );
    }

}
