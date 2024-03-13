package persistence.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EntityKeyTest {

    @Test
    @DisplayName("new EntityKey 생성시 다른 key로 인식해야 한다")
    public void entityKeyTest() {
        HashMap<EntityKey, String> map = new HashMap<>();
        EntityKey entityKey1 = new EntityKey(EntityKeyTest.class, 1L);
        EntityKey entityKey2 = new EntityKey(EntityKeyTest.class, 1L);

        map.put(entityKey1, "test");
        map.put(entityKey2, "test");

        assertNotEquals(entityKey1, entityKey2);
        assertNull(map.get(new EntityKey(EntityKeyTest.class, 1L)));
    }

}
