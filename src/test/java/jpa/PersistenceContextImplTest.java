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

        Person persistencePerson = persistenceContext.get(Person.class, id);

        assertAll(() -> {
            assertThat(persistencePerson.getId()).isEqualTo(id);
            assertThat(persistencePerson.getEmail()).isEqualTo(email);
            assertThat(persistencePerson.getAge()).isEqualTo(age);
        });
    }

    @Test
    void 데이터_1차캐시_생성_및_제거() {
        Long id = 11L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);


        persistenceContext.remove(person);

        assertThat(persistenceContext.get(Person.class, id)).isNull();
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
        persistenceContext.add(person);
        Person updatedPerson = persistenceContext.get(Person.class, id);

        assertThat(updatedPerson.getEmail()).isEqualTo(updateEmail);
    }

    @Test
    void 데이터_생성_후_더티_체킹() {
        Long id = 10L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);
        persistenceContext.createDatabaseSnapshot(person);

        String updateEmail = "test@test.com";
        person.setEmail(updateEmail);
        Person snapshotPerson = persistenceContext.getDatabaseSnapshot(person);
        assertThat(snapshotPerson.getEmail()).isEqualTo(email);
        assertThat(person.getEmail()).isEqualTo(updateEmail);;
    }

    @Test
    void 데이터_생성_후_더티_체킹_유무_확인() {
        Long id = 10L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);
        persistenceContext.createDatabaseSnapshot(person);

        String updateEmail = "test@test.com";
        person.setEmail(updateEmail);

        assertThat(persistenceContext.isDirty(person)).isTrue();
    }

    @Test
    void 컨텍스트_추가_후_상태_확인() {
        Long id = 10L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);
        persistenceContext.createDatabaseSnapshot(person);

        assertThat(persistenceContext.getEntityEntry(person)).extracting("entityStatus").isEqualTo(EntityStatus.MANAGED);
    }

    @Test
    void 삭제_후_상태_확인() {
        Long id = 10L;
        String name = "이름";
        String email = "email@gmail.com";
        int age = 10;

        Person person = new Person(id, name, age, email);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        persistenceContext.add(person);
        persistenceContext.createDatabaseSnapshot(person);

        persistenceContext.remove(person);

        assertThat(persistenceContext.getEntityEntry(person)).extracting("entityStatus").isEqualTo(EntityStatus.GONE);
    }


}
