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
        EntityMetaManager entityMetaManager = EntityMetaManager.getInstance();
//        entityMetaManager.loadEntities();

        assertAll(
                () -> assertNotNull(entityMetaManager.getEntityMetadata(Person.class)),
                () -> assertThat(entityMetaManager.getEntityMetadata(Person.class)).isNotNull()
        );
    }

}
