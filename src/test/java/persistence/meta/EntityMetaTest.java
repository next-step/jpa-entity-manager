package persistence.meta;


import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.NoEntityException;
import persistence.testFixtures.NoHasEntity;
import persistence.testFixtures.Person;

class EntityMetaTest {

    @Test
    @DisplayName("엔티티가 비어 있으면 예외가 발생한다")
    void emptyEntity() {
       assertThatExceptionOfType(NoEntityException.class)
               .isThrownBy(() -> EntityMeta.from(null));
    }

    @Test
    @DisplayName("엔티티 어노테이션이 없으면 예외를 발생한다.")
    void noEntity() {
        assertThatExceptionOfType(NoEntityException.class)
                .isThrownBy(() -> EntityMeta.from(NoHasEntity.class));
    }

    @Test
    @DisplayName("엔티티 어노테이션이 없으면 예외를 발생한다.")
    void createEntityMeta() {
        EntityMeta entityMeta = EntityMeta.from(Person.class);

        assertSoftly((it -> {
            it.assertThat(entityMeta.getTableName()).isEqualTo("users");
            it.assertThat(entityMeta.getEntityColumns()).hasSize(4);
        }));
    }

    @Test
    @DisplayName("엔티티를 복사 생성한다.")
    void createEntityInstance() {
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        Person person = new Person(1L, "이름", 19, "data@gmail.com");
        final Person copyEntity = entityMeta.createCopyEntity(person);

        assertSoftly((it -> {
            it.assertThat(copyEntity == person).isFalse();
            it.assertThat(copyEntity).isEqualTo(person);
        }));
    }

}
