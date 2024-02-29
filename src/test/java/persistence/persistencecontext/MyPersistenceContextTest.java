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
        persistenceContext.addEntity(EntityMeta.from(person));

        //then
        assertThat(persistenceContext.getEntity(Person.class, 1L)).isNotNull();
    }

    @Test
    void getEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        String expectedName = "ABC";
        Person person = new Person(1L, expectedName, 10, "ABC@email.com", 10);
        persistenceContext.addEntity(EntityMeta.from(person));

        //when
        Person actual = persistenceContext.getEntity(Person.class, 1L).get();

        //then
        assertThat(actual).extracting("name").isEqualTo(expectedName);
    }

    @Test
    void removeEntity() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        persistenceContext.addEntity(EntityMeta.from(person));

        //when
        persistenceContext.removeEntity(EntityMeta.from(person));

        //then
        assertThat(persistenceContext.getEntity(Person.class, 1L).isEmpty()).isTrue();
    }

    @Test
    void getDatabaseSnapshot() {
        //given
        MyPersistenceContext persistenceContext = new MyPersistenceContext();
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        persistenceContext.getDatabaseSnapshot(EntityMeta.from(person));

        //when
        EntitySnapshot actual = persistenceContext.getDatabaseSnapshot(EntityMeta.from(person));

        //then
        assertThat(actual.getSnapshot()).isNotEmpty();
    }
}
