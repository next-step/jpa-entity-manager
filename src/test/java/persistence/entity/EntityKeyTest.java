package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityKeyTest {
    private EntityKey key1;
    private EntityKey key2;

    @BeforeEach
    void setUp() {
        key1 = new EntityKey(Person.class, 1);
        key2 = new EntityKey(Person.class, 1);
    }

    @Test
    @DisplayName("EntityKey 가 같다면, equals 가 true 여야 한다.")
    void testEquals() {
        assertThat(
                key1.equals(key2)
        ).isTrue();
    }

    @Test
    @DisplayName("EntityKey 가 같다면, 같은 hash 값이 생성되어야 한다.")
    void testHashCode() {
        assertThat(
                key1.hashCode()
        ).isEqualTo(key2.hashCode());
    }
}
