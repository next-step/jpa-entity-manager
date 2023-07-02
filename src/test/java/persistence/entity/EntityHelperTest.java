package persistence.entity;

import domain.PersonFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityHelperTest {
    private Object entity;

    @BeforeEach
    void setUp() {
        entity = PersonFixture.createPerson();
    }

    @Test
    @DisplayName("임의의 객체를 clone 할 수 있다.")
    void testClone() {
        assertThat(
                entity != EntityHelper.clone(entity)
        ).isTrue();
    }

    @Test
    @DisplayName("임의의 객체의 내부 상태값을 비교할 수 있다.")
    void testEquals() {
        Object snapshot = EntityHelper.clone(entity);
        assertThat(
                EntityHelper.equals(entity, snapshot)
                        && entity != snapshot
        ).isTrue();
    }
}
