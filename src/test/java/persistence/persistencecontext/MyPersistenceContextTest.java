package persistence.persistencecontext;

import org.junit.jupiter.api.Test;
import persistence.entity.EntityMeta;
import persistence.sql.Person;

import static org.assertj.core.api.Assertions.assertThat;

class MyPersistenceContextTest {

    @Test
    void addEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);

        //when
        persistenceContext.addEntity(person);

        //then
        assertThat(persistenceContext.getEntity(Person.class, 1L)).isNotNull();
    }

    @Test
    void getEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        String expectedName = "ABC";
        Person person = new Person(1L, expectedName, 10, "ABC@email.com", 10);
        persistenceContext.addEntity(person);

        //when
        Object actual = persistenceContext.getEntity(Person.class, 1L).get();

        //then
        assertThat(actual).extracting("name").isEqualTo(expectedName);
    }

    @Test
    void removeEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        persistenceContext.addEntity(person);

        //when
        persistenceContext.removeEntity(person);

        //then
        assertThat(persistenceContext.getEntity(Person.class, 1L).isEmpty()).isTrue();
    }

    @Test
    void getDatabaseSnapshot() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        persistenceContext.getDatabaseSnapshot(person);

        //when
        EntitySnapshot actual = persistenceContext.getDatabaseSnapshot(person);

        //then
        assertThat(actual.getSnapshot()).isNotEmpty();
    }
}
