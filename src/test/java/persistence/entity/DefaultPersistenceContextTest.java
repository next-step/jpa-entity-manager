package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.testFixtures.Person;

class DefaultPersistenceContextTest {

    @Test
    @DisplayName("영속성 컨텍스트에 의해 저장 및 조회가 된다.")
    void getEntity() {
        //given
        Person person = new Person(1L ,"이름", 19, "asd@gmail.com");
        PersistenceContext context = new DefaultPersistenceContext(new EntityMeta(person.getClass()));

        //when
        context.addEntity(1L, person);

        //then
        assertThat(context.getEntity(1L)).isEqualTo(person);
    }

    @Test
    @DisplayName("영속성 컨텍스트에서 삭제가 된다.")
    void removeEntity() {
        //given
        Person person = new Person(1L ,"이름", 19, "asd@gmail.com");
        PersistenceContext context = new DefaultPersistenceContext(new EntityMeta(person.getClass()));

        //when
        context.addEntity(1L, person);
        context.removeEntity(person);

        //then
        assertThat(context.getEntity(1L)).isNull();
    }


}
