package persistence.entity.persistencecontext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.entity.persistencecontext.EntityPersistIdentity;

import static org.assertj.core.api.Assertions.*;

class EntityPersistIdentityTest {

    @DisplayName("class와 entityId가 같으면 같은 EntityPersistIdentity 객체이다. (동등성 비교)")
    @Test
    void equals() {
        EntityPersistIdentity id = new EntityPersistIdentity(Person.class, 1L);
        EntityPersistIdentity sameId = new EntityPersistIdentity(Person.class, 1L);
        EntityPersistIdentity notSameId1ByEntityId = new EntityPersistIdentity(Person.class, 2L);
        EntityPersistIdentity notSameId2ByEntityClass = new EntityPersistIdentity(String.class, 1L);

        assertThat(id).isEqualTo(sameId)
                .isNotEqualTo(notSameId1ByEntityId)
                .isNotEqualTo(notSameId2ByEntityClass);
    }
}
