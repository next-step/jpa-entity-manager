package hibernate.entity.meta.column;

import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.meta.column.EntityColumns;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityColumnsTest {

    @Test
    void Transient가_없는_필드로_생성한다() {
        EntityColumns actual = new EntityColumns(TestEntity.class.getDeclaredFields());
        assertThat(actual.getValues()).hasSize(2);
    }

    @Test
    void EntityId를_반환한다() {
        EntityColumns givenEntityColumns = new EntityColumns(TestEntity.class.getDeclaredFields());
        EntityColumn actual = givenEntityColumns.getEntityId();
        assertThat(actual.isId()).isTrue();
    }

    @Test
    void EntityId가_없는_경우_예외가_발생한다() {
        EntityColumns givenEntityColumns = new EntityColumns(NoIdTestEntity.class.getDeclaredFields());
        assertThatThrownBy(givenEntityColumns::getEntityId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("id field가 없습니다.");
    }

    @Test
    void 객체의_필드값을_매핑하여_가져온다() {
        EntityColumns entityColumns = new EntityColumns(TestEntity.class.getDeclaredFields());
        Map<EntityColumn, Object> actual = entityColumns.getFieldValues(new TestEntity(1L, "최진영", "jinyoungchoi95@gmail.com"));
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> {
                    assert actual != null;
                    assertThat(actual.values()).contains(1L, "최진영");
                }
        );
    }

    static class TestEntity {
        @Id
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    static class NoIdTestEntity {
        private String name;
    }
}
