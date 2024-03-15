package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.PersistenceContext;
import persistence.sql.ddl.entity.Person;

class PersistenceContextImplTest {
    private PersistenceContext persistenceContext;

    private static final List<Person> givenPersons = List.of(
        new Person(1L, "user1", 10, "test1@gmail.com"),
        new Person(2L, "user2", 20, "test2@gmail.com"),
        new Person(3L, "user3", 30, "test3@gmail.com")
    );

    @BeforeEach
    void setUp() {
        persistenceContext = new PersistenceContextImpl();

        givenPersons.forEach(givenPerson -> persistenceContext.addEntity(givenPerson.getId(), givenPerson));
    }

    @DisplayName("요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자 (getEntity)")
    @ParameterizedTest
    @MethodSource("providePerson")
    void getEntity(Person givenPerson) {
        // given
        Long givenPersonId = givenPerson.getId();

        // when
        Object entity = persistenceContext.getEntity(Person.class, givenPersonId);

        // then
        assertAll(
            () -> assertThat(entity).isInstanceOf(Person.class),
            () -> assertThat(entity).isEqualTo(givenPerson)
        );
    }

    @DisplayName("요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자 (addEntity)")
    @Test
    void addEntity() {
        // given
        Person givenPerson = new Person(4L, "user4", 40, "test4@gmail.com");

        // when
        persistenceContext.addEntity(givenPerson.getId(), givenPerson);

        // then
        Object getEntityResult = persistenceContext.getEntity(Person.class, givenPerson.getId());

        assertThat(getEntityResult).isEqualTo(givenPerson);
    }

    @DisplayName("요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자 (removeEntity)")
    @ParameterizedTest
    @MethodSource("providePerson")
    void removeEntity(Person givenPerson) {
        // when
        persistenceContext.removeEntity(givenPerson);

        // then
        Object getEntityResult = persistenceContext.getEntity(Person.class, givenPerson.getId());

        assertThat(getEntityResult).isNull();
    }

    private static Stream<Arguments> providePerson() {
        return givenPersons.stream()
            .map(Arguments::of);
    }

    @DisplayName("요구사항2 - snapshot 만들기")
    @Test
    void getDatabaseSnapshot() {
        // given
        Person givenPerson = new Person(1L, "user1", 10, "test@gmail.com");
        Person changedPerson = clonePerson(givenPerson);
        changedPerson.changeEmail("change@gmail.com");

        // when
        Person databaseSnapshot = persistenceContext.getDatabaseSnapshot(givenPerson.getId(), givenPerson);

        // then
        assertAll(
            () -> assertThat(databaseSnapshot).isEqualTo(givenPerson),
            () -> assertThat(databaseSnapshot).isNotEqualTo(changedPerson)
        );
    }

    private Person clonePerson(Person person) {
        return new Person(person.getId(), person.getName(), person.getAge(), person.getEmail());
    }
}
