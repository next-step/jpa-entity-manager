package persistence.entity;



import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.testFixtures.Person;
import persistence.testFixtures.PkHasPerson;

@DisplayName("EntityKey 테스트 (영속성 컨텍스트키)")
class EntityKeyTest {

    @Test
    @DisplayName("EntityKey 생성")
    void createEntityKey() {
        //given
        final Long id = 1L;
        final Person person = new Person(id, "이름", 19, "sada@gmail.com");

        //when
        EntityKey entityKey = EntityKey.of(person);
        EntityKey entityKey1 = EntityKey.of(Person.class, id);

        //then
        assertSoftly((it) -> {
            it.assertThat(entityKey).isEqualTo(entityKey1);
            it.assertThat(entityKey.hashCode()).isEqualTo(entityKey1.hashCode());
        });
    }


    @Test
    @DisplayName("엔티티 ID가 다르면 다른 EntityKey 이다")
    void notEqualsIdEntityKey() {
        //given
        final Person person = new Person(1L, "이름", 19, "sada@gmail.com");
        final Person person2 = new Person(2L, "이름", 19, "sada@gmail.com");

        //when
        EntityKey entityKey = EntityKey.of(person);
        EntityKey entityKey2 = EntityKey.of(person2);

        //then
        assertSoftly((it) -> {
            it.assertThat(entityKey).isNotEqualTo(entityKey2);
            it.assertThat(entityKey.hashCode()).isNotEqualTo(entityKey2.hashCode());
        });
    }
    @Test
    @DisplayName("엔티티 클래스 다르면 다른 EntityKey 이다")
    void notEqualsClassEntityKey() {
        //given
        final Long id = 1L;
        final Person person = new Person(id, "이름", 19, "sada@gmail.com");
        final PkHasPerson person2 = new PkHasPerson(id, "이름", 19);

        //when
        EntityKey entityKey = EntityKey.of(person);
        EntityKey entityKey2 = EntityKey.of(person2);

        //then
        assertSoftly((it) -> {
            it.assertThat(entityKey).isNotEqualTo(entityKey2);
            it.assertThat(entityKey.hashCode()).isNotEqualTo(entityKey2.hashCode());
        });
    }

    @Test
    @DisplayName("엔티티 ID가 같으면 같은 EntityKey 이다")
    void idEqualEntityKey() {
        //given
        final Long id = 1L;
        final Person person = new Person(id, "이름", 19, "sada@gmail.com");
        final Person person2 = new Person(id, "2", 19, "sada@gmail.com");

        //when
        EntityKey entityKey = EntityKey.of(person);
        EntityKey entityKey2 = EntityKey.of(person2);

        //then
        assertSoftly((it) -> {
            it.assertThat(entityKey).isEqualTo(entityKey2);
            it.assertThat(entityKey.hashCode()).isEqualTo(entityKey2.hashCode());
        });
    }
}
