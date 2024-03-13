package persistence.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EntityKeyManagerTest {

    EntityKeyManager entityKeyManager = new EntityKeyManager();

    @Test
    @DisplayName("entity의 식별자정보가 저장된다.")
    public void createKey() {
        EntityKey entityKey = entityKeyManager.createKey(Person.class, 1L);
        EntityKey getEntityKey = entityKeyManager.from(Person.class, 1L);

        assertEquals(entityKey, getEntityKey);
    }



}
