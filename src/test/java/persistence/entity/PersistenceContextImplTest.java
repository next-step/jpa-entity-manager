package persistence.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceContextImplTest {

    @Test
    void should_do_first_level_cache(){
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl(new SimpleSnapshotStorage());

        Person person = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        persistenceContext.addEntity(person);
        Person foundEntity = persistenceContext.getEntity(Person.class, 1L);

        assertThat(person).isEqualTo(foundEntity);
    }
    @Test
    void should_remove_cache(){
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl(new SimpleSnapshotStorage());

        Person person = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        persistenceContext.addEntity(person);
        persistenceContext.removeEntity(person);
        Person foundEntity = persistenceContext.getEntity(Person.class, 1L);

        assertThat(foundEntity).isNull();
    }

}
