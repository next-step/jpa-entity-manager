package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

    @Test
    @DisplayName("영속성 컨텍스트에서 스냅샷을 만들어서 저장한다.")
    void getDatabaseSnapshot() {
        //given
        Person person = new Person(1L, "이름", 19, "sad@gmail.com");
        PersistenceContext context = new DefaultPersistenceContext(new EntityMeta(person.getClass()));


        //when
        context.addEntity(1L, person);
        context.getDatabaseSnapshot(1L, person);
        person.changeEmail("변경이메일@gamil.com");
        final Person entity = (Person) context.getEntity(1L);
        final Person snapshot = (Person) context.getCachedDatabaseSnapshot(1L);

        //then
        assertSoftly((it) -> {
            it.assertThat(snapshot.getEmail()).isNotEqualTo("변경이메일@gamil.com");
            it.assertThat(entity.getEmail()).isNotEqualTo(snapshot.getEmail());
            it.assertThat(entity).isNotEqualTo(snapshot);
        });
    }

    @Test
    @DisplayName("영속성 컨텍스트에서 변경된 엔티티를 조회한다.")
    void getChangedEntity() {
        //given
        Person person = new Person(1L, "이름", 19, "sad@gmail.com");
        Person person2 = new Person(2L, "이름", 19, "sad@gmail.com");
        PersistenceContext context = new DefaultPersistenceContext(new EntityMeta(person.getClass()));


        //when
        context.addEntity(1L, person);
        context.addEntity(2L, person2);
        context.getDatabaseSnapshot(1L, person);
        context.getDatabaseSnapshot(2L, person2);
        person.changeEmail("변경이메일@gamil.com");

        //then
        assertThat(context.getChangedEntity()).contains(person);
        assertThat(context.getChangedEntity()).doesNotContain(person2);
    }
}
