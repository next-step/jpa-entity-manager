package persistence.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

class EntityMetaManagerTest {

    @Test
    @DisplayName("EntityLoader를 통해 EntityMetadata 정보가 로드되어야 한다.")
    public void loadEntities() {
        EntityMetaManager.loadEntities();

        assertAll(
                () -> assertNotNull(EntityMetaManager.getEntityMetadata(Person.class)),
                () -> assertThat(EntityMetaManager.getEntityMetadata(Person.class)).isNotNull()
        );
    }

}
