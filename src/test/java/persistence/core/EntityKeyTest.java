package persistence.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EntityKeyTest {

    @Test
    public void entityKeyTest() {
        HashMap<EntityKey, String> map = new HashMap<>();
        EntityKey entityKey = new EntityKey(EntityKeyTest.class, 1L);
        EntityKey entityKey2 = new EntityKey(EntityKeyTest.class, 1L);

        map.put(entityKey, "test");
        map.put(entityKey2, "test");

        String s = map.get(new EntityKey(EntityKeyTest.class, 1L));


        System.out.println(s);


    }

}
