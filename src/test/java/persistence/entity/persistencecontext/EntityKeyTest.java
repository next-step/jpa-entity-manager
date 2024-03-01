package persistence.entity.persistencecontext;

import static org.junit.jupiter.api.Assertions.*;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.persistencecontext.EntityKey;

@DisplayName("EntityKey class의")
class EntityKeyTest {

    @DisplayName("equals 메서드는")
    @Nested
    class Equals {
        @DisplayName("두 EntityKey가 같은 테이블과 같은 id를 가지고 있으면 true를 반환한다.")
        @Test
        void it_returns_true_if_two_entity_keys_have_same_table_and_same_id() {
            // given
            EntityKey entityKey1 = EntityKey.of(Person.class, 1L);
            EntityKey entityKey2 = EntityKey.of(Person.class, 1L);
            // when
            boolean result = entityKey1.equals(entityKey2);
            // then
            assertTrue(result);
        }
    }

    @DisplayName("hashCode 메서드는")
    @Nested
    class Describe_hashCode {
        @DisplayName("두 EntityKey가 같은 테이블과 같은 id를 가지고 있으면 같은 해시코드를 반환한다.")
        @Test
        void it_returns_same_hash_code_if_two_entity_keys_have_same_table_and_same_id() {
            // given
            EntityKey entityKey1 = EntityKey.of(Person.class, 1L);
            EntityKey entityKey2 = EntityKey.of(Person.class, 1L);
            // when
            int hashCode1 = entityKey1.hashCode();
            int hashCode2 = entityKey2.hashCode();
            // then
            assertEquals(hashCode1, hashCode2);
        }
    }
}
