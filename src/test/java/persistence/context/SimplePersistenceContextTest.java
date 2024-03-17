package persistence.context;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePersistenceContextTest {

    private SimplePersistenceContext persistenceContext;

    @BeforeEach
    void setUp() {
        persistenceContext = new SimplePersistenceContext();
    }

    @Test
    @DisplayName("Entity가 Persistent 시 상태가 Managed인지 확인한다.")
    void entityPersistent_ThenEntityStatusIsManaged() {
        // given
        final Person person = Person.of(1L, "crong", 35, "test12@gmail.com");
        persistenceContext.addEntity(1L, person);

        // when
        // then
        assertThat(persistenceContext.getEntry(person).getStatus()).isEqualTo(EntityStatus.MANAGED);
    }

    @Test
    @DisplayName("Persistent 상태의 Entity를 제거하면 상태가 Gone인지 확인한다.")
    void entityPersistent_With_EntityStatus_Managed() {
        // given
        final Person person = Person.of(1L, "crong", 35, "test12@gmail.com");
        persistenceContext.addEntity(1L, person);

        // when
        persistenceContext.removeEntity(person);

        // then
        assertThat(persistenceContext.getEntry(person).getStatus()).isEqualTo(EntityStatus.GONE);
    }

}