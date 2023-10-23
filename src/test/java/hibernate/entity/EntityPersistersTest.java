package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import hibernate.dml.UpdateQueryBuilder;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityPersistersTest {

    private final EntityPersister<TestEntity> entityPersister = new EntityPersister<>(
            TestEntity.class,
            null,
            new InsertQueryBuilder(),
            new DeleteQueryBuilder(),
            new UpdateQueryBuilder()
    );
    private final EntityPersisters entityPersisters = new EntityPersisters(Map.of(TestEntity.class, entityPersister));

    @Test
    void Entity에_맞는_EntityPersister를_반환한다() {
        EntityPersister<?> actual = entityPersisters.findEntityPersister(new TestEntity());
        assertThat(actual).isEqualTo(entityPersister);
    }

    @Test
    void Entity에_맞는_EntityPersister가_없는_경우_예외가_발생한다() {
        assertThatThrownBy(() -> entityPersisters.findEntityPersister(new NoExistEntity()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 Entity 클래스입니다.");
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public TestEntity() {
        }
    }

    @Entity
    @Table(name = "no_exist_entity")
    static class NoExistEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }
}
