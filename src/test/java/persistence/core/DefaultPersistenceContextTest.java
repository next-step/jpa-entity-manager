package persistence.core;

import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPersistenceContextTest {

    DefaultPersistenceContext defaultPersistenceContext = new DefaultPersistenceContext();

    @Test
    public void addEntityTest() {
        Person person = new Person();
        defaultPersistenceContext.addEntity(1L, person);

        assertEquals(person, defaultPersistenceContext.getEntity(1L));
    }

    @Test
    public void removeEntityTest() {
        Person person = new Person();
        defaultPersistenceContext.removeEntity(person);

        assertNull(defaultPersistenceContext.getEntity(1L));
    }

}
