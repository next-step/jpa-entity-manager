package hibernate.entity.persistencecontext;

import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntitySnapshotTest {

    @Test
    void entity를_받아_snapshot을_생성한다() {
        TestEntity givenEntity = new TestEntity(1L, "최진영", "jinyoungchoi95@gmail.com");
        EntitySnapshot actual = new EntitySnapshot(givenEntity);
        givenEntity.name = "영진최";
        EntityColumn entityColumn = EntityClass.getInstance(TestEntity.class)
                .getEntityColumns()
                .stream()
                .filter(it -> it.getFieldName() == "name")
                .findAny()
                .get();


        assertAll(
                () -> assertThat(actual.getSnapshot()).hasSize(3),
                () -> assertThat(actual.getSnapshot().get(entityColumn)).isEqualTo("최진영"),
                () -> assertThat(givenEntity.name).isEqualTo("영진최")
        );
    }

    @Test
    void 스냅샷과_비교해_변경된_필드를_반환한다() {
        EntitySnapshot snapshot = new EntitySnapshot(new TestEntity(1L, "최진영", "jinyoungchoi95@gmail.com"));
        Map<EntityColumn, Object> actual = snapshot.changedColumns(new TestEntity(1L, "영진최", "jinyoungchoi95@gmail.com"));
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.values()).contains("영진최")
        );
    }

    @Test
    void 스냅샷과_비교해_변경된_필드가_없는_경우_빈_map을_반환한다() {
        EntitySnapshot snapshot = new EntitySnapshot(new TestEntity(1L, "최진영", "jinyoungchoi95@gmail.com"));
        Map<EntityColumn, Object> actual = snapshot.changedColumns(new TestEntity(1L, "최진영", "jinyoungchoi95@gmail.com"));
        assertThat(actual).isEmpty();
    }

    @Entity
    static class TestEntity {
        @Id
        private Long id;

        private String name;

        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}
