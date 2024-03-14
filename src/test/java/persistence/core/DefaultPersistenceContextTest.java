package persistence.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPersistenceContextTest {

    DefaultPersistenceContext defaultPersistenceContext = new DefaultPersistenceContext();

    EntityKey entityKey = defaultPersistenceContext.getEntityKey(Person.class, 1L);
    @Test
    @DisplayName("persistenceContext에 entity추가 테스트")
    public void addEntityTest() {
        Person person = new Person();
        person.setId(1L);

        defaultPersistenceContext.addEntity(entityKey, person);

        assertEquals(person, defaultPersistenceContext.getEntity(entityKey));
    }

    @Test
    @DisplayName("persistenceContext에 entity제거 테스트")
    public void removeEntityTest() {
        Person person = new Person();
        person.setId(1L);

        defaultPersistenceContext.removeEntity(entityKey);

        assertNull(defaultPersistenceContext.getEntity(entityKey));
    }

    @Test
    @DisplayName("entity snapshot 생성 테스트")
    public void getDatabaseSnapshotTest() {
        Person person = new Person();
        person.setId(1L);
        person.setName("test");
        person.setAge(20);

        defaultPersistenceContext.addEntity(entityKey, person);
        defaultPersistenceContext.getDatabaseSnapshot(entityKey);

        Snapshot snapshot = defaultPersistenceContext.getSnapshot(entityKey);
        Map<String, Object> snapShot = snapshot.get();

        assertEquals(person.getId(), snapShot.get("id"));
        assertEquals(person.getName(), snapShot.get("nick_name"));
        assertEquals(person.getAge(), snapShot.get("old"));
    }

    @Test
    @DisplayName("dirtyCheck 테스트")
    public void dirtyCheckTest() {
        Person person = new Person();
        person.setId(1L);
        person.setName("test");
        person.setAge(20);

        defaultPersistenceContext.addEntity(entityKey, person);
        defaultPersistenceContext.getDatabaseSnapshot(entityKey);

        person.setName("test2");
        List<Object> objects = defaultPersistenceContext.dirtyCheck();

        assertEquals(1, objects.size());
    }
}
