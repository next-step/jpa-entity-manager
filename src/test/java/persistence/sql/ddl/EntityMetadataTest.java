package persistence.sql.ddl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Fixtures;
import persistence.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityMetadataTest {

    @Test
    @DisplayName("@Entity 없으면 Exception")
    public void noEntityAnnotation() {

        assertThatThrownBy(() -> {
            new EntityMetadata(NoEntityAnnotation.class);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No @Entity annotation");
    }

    @Test
    @DisplayName("setIdToEntity() 메서드 테스트")
    public void setIdToEntity() {
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);
        // TODO 픽스처 하나 만들어서 걷어내자.
        Person person = Fixtures.person1();

        entityMetadata.setIdToEntity(person, 1L);

        assertThat(person.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("setIdToEntity() 메서드 Exception 테스트")
    public void setIdToEntityException() {
        EntityMetadata entityMetadata = new EntityMetadata(EntityAnnotation.class);
        EntityAnnotation entityAnnotation = new EntityAnnotation();

        assertThatThrownBy(() -> {
            entityMetadata.setIdToEntity(entityAnnotation, 1L);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("Entity 객체에 ID 값을 세팅 중 오류 발생");
    }

    private class NoEntityAnnotation {
    }

    @Entity
    private class EntityAnnotation {

        @Id
        private Integer id;

    }

}
