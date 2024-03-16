package persistence.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

class EntityKeyManagerTest {

    EntityKeyManager entityKeyManager = new EntityKeyManager();

    @Test
    @DisplayName("entity의 식별자정보가 저장된다.")
    public void createKey() {
        EntityKey entityKey = entityKeyManager.from(Person.class, 1L);
        EntityKey getEntityKey = entityKeyManager.from(Person.class, 1L);

        assertEquals(entityKey, getEntityKey);
    }



}
