package persistence.entity;

import org.junit.jupiter.api.Test;
import persistence.sql.entity.Person;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextImplTest {

    @Test
    void should_do_first_level_cache() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl(new SimpleSnapshotStorage());

        Person person = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        persistenceContext.addEntity(person);
        Person foundEntity = persistenceContext.getEntity(Person.class, 1L);

        assertThat(person).isEqualTo(foundEntity); // 동등성 검증
    }

    @Test
    void first_cache_equality_test() throws IllegalAccessException {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl(new SimpleSnapshotStorage());

        Person person = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        persistenceContext.addEntity(person);
        Person foundEntity = persistenceContext.getEntity(Person.class, 1L);

        Class<Person> clazz = Person.class;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(person);
            Object foundValue = field.get(foundEntity);
            assertThat(value).isEqualTo(foundValue);
        }

    }

    @Test
    void should_remove_cache() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl(new SimpleSnapshotStorage());

        Person person = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        persistenceContext.addEntity(person);
        persistenceContext.removeEntity(person);
        Person foundEntity = persistenceContext.getEntity(Person.class, 1L);

        assertThat(foundEntity).isNull();
    }

}
