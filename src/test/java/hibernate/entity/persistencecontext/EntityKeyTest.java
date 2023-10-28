package hibernate.entity.persistencecontext;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityKeyTest {

    @Test
    void id가_일치하면_동등성을_보장한다() {
        EntityKey key1 = new EntityKey(1L, TestEntity1.class);
        EntityKey key2 = new EntityKey(1L, TestEntity1.class);
        assertThat(key1.equals(key2)).isTrue();
    }

    @Test
    void id가_일치하지_않으면_동등성은_실패한다() {
        EntityKey key1 = new EntityKey(1L, TestEntity1.class);
        EntityKey key2 = new EntityKey(2L, TestEntity1.class);
        assertThat(key1.equals(key2)).isFalse();
    }

    @Test
    void EntityClass가_일치하지_않으면_동등성은_실패한다() {
        EntityKey key1 = new EntityKey(1L, TestEntity1.class);
        EntityKey key2 = new EntityKey(1L, TestEntity2.class);
        assertThat(key1.equals(key2)).isFalse();
    }

    @Entity
    static class TestEntity1 {
        @Id
        private Long id;
    }

    @Entity
    static class TestEntity2 {
        @Id
        private Long id;
    }
}
