package persistence.entity;


import domain.FixturePerson;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityCacheTest {
    private EntityCache entityCache;
    private Person fixture;
    private EntityKey entityKey;

    @BeforeEach
    void setUp() {
        entityCache = new EntityCache();
        fixture = FixturePerson.create(1L);
        entityKey = new EntityKey(Person.class, 1L);

    }

    @Test
    @DisplayName("entityCache.get 를 통해 없는 Entity 조회시 Optional.empty 를 반환한다.")
    void entityCacheNotExistTest() {
        final Optional<Object> result = entityCache.get(entityKey);

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("entityCache 를 통해 Entity 들을 저장하고 조회할 수 있다.")
    void entityCacheTest() {
        entityCache.add(entityKey, fixture);

        final Optional<Object> result = entityCache.get(entityKey);

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("entityCache.remove 를 통해 저장된 Entity 를 제거할 수 있다.")
    void entityCacheRemoveTest() {
        entityCache.add(entityKey, fixture);

        entityCache.remove(entityKey);

        final Optional<Object> result = entityCache.get(entityKey);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("entityCache.containsKey 를 통해 cache 에 해당 key 가 존재하는지 여부를 반환 받을 수 있다.")
    void entityCacheContainsKeyTest() {
        final EntityKey newKey = new EntityKey(Person.class, 2L);

        entityCache.add(entityKey, fixture);

        assertSoftly(softly->{
            softly.assertThat(entityCache.containsKey(entityKey)).isTrue();
            softly.assertThat(entityCache.containsKey(newKey)).isFalse();
        });
    }

}
