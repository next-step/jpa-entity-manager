package jpa;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;
import sql.ddl.JdbcServerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcServerTest
class PersistenceContextImplTest {
    @Test
    void 데이터_1차캐시_추가() {
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;
        int index = 1;
        Person person = new Person(name, age, email, index);

        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);
    }

    @Test
    void 데이터_1차캐시_생성_및_조회() {
        Long id = 1L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;
        Person person = new Person(id, name, age, email);

        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);

        EntityInfo<Person> entityInfo = new EntityInfo<>(Person.class, id);
        Person persistencePerson = Person.class.cast(persistenceContext.get(entityInfo));

        assertAll(() -> {
            assertThat(persistencePerson.getId()).isEqualTo(id);
            assertThat(persistencePerson.getEmail()).isEqualTo(email);
            assertThat(persistencePerson.getAge()).isEqualTo(age);
        });
    }

    @Test
    void 데이터_1차캐시_생성_및_제거() {
        Long id = 1L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);


        persistenceContext.remove(person);

        assertThat(persistenceContext.contain(new EntityInfo<>(Person.class, id))).isFalse();
    }

    @Test
    void 데이터_업데이트() {
        Long id = 1L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);

        String updateEmail = "update@gmail.com";
        person.setEmail(updateEmail);
        persistenceContext.update(person);
        Person updatedPerson = Person.class.cast(persistenceContext.get(new EntityInfo<>(Person.class, id)));

        assertThat(updatedPerson.getEmail()).isEqualTo(updateEmail);
    }
}
