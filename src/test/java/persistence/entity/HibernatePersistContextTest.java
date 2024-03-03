package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.dialect.MysqlDialect;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HibernatePersistContextTest {

    private PersistenceContext context;

    @BeforeEach
    void setUp() {
        context = new HibernatePersistContext();
    }

    @DisplayName("PersistContext에 Entity를 추가하고 조회한다. - getEntity, addEntity")
    @Test
    void getEntity() {
        //given
        String name = "John";
        String email = "John@test.com";
        Person person = new Person(name, email, 30);
        long id = 1L;
        context.addEntity(person, id);

        //when
        Person findPerson = context.getEntity(Person.class, id).get();

        //then
        assertAll(
                () -> assertThat(findPerson.getName()).isEqualTo(name),
                () -> assertThat(findPerson.getEmail()).isEqualTo(email)
        );
    }

    @DisplayName("PersistContext에 Entity를 삭제한다.")
    @Test
    void removeEntity() {
        //given
        Person person = new Person("John", "John@test.com", 30);
        long id = 1L;
        EntityMetaData entityMetaData = new EntityMetaData(person, new MysqlDialect());
        context.addEntity(entityMetaData, id);

        //when
        context.removeEntity(person.getClass(), id);

        //then
        assertThat(context.getEntity(Person.class, id)).isEmpty();
    }
}
