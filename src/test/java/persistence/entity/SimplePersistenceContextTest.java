package persistence.entity;

import domain.FixturePerson;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SimplePersistenceContextTest {

    private PersistenceContext persistenceContext;
    private EntityKeyGenerator entityKeyGenerator;
    private EntityKey personEntityKey;
    private Person person;

    @BeforeEach
    void setUp() {
        persistenceContext = new SimplePersistenceContext();
        entityKeyGenerator = new EntityKeyGenerator();
        personEntityKey = entityKeyGenerator.generate(Person.class, 1L);
        person = FixturePerson.create(1L);
        persistenceContext.addEntityEntry(person, Status.LOADING);
    }

    @Test
    @DisplayName("addEntity 를 통해 persistenceContext 에 entity 를 저장할 수 있다.")
    void addEntityTest() {
        persistenceContext.addEntity(personEntityKey, person);

        final Object entity = persistenceContext.getEntity(entityKeyGenerator.generate(Person.class, 1L)).orElse(null);
        assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity).isInstanceOf(Person.class);
            softly.assertThat(((Person) entity).getId()).isEqualTo(1L);
        });

    }

    @Test
    @DisplayName("같은 key로 getEntity 를 통해 조회한 Entity 는 항상 동일한 객체이다.")
    void getEntityTest() {
        persistenceContext.addEntity(personEntityKey, person);
        final Object entity = persistenceContext.getEntity(personEntityKey).orElse(null);
        final Object entity2 = persistenceContext.getEntity(personEntityKey).orElse(null);

        assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity2).isNotNull();
            softly.assertThat(entity == entity2).isTrue();
        });
    }

    @Test
    @DisplayName("remove 를 통해 Entity 를 PersistenceContext 에서 제거할 수 있다..")
    void removeEntityTest() {
        persistenceContext.addEntity(personEntityKey, person);

        persistenceContext.removeEntity(personEntityKey);

        final Object entity = persistenceContext.getEntity(personEntityKey).orElse(null);
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("getDatabaseSnapshot 를 통해 persistenceContext 의 EntitySnapShot 에 entity 를 저장할 수 있다.")
    void getDatabaseSnapshotTest() {
        final Object entity = persistenceContext.getDatabaseSnapshot(personEntityKey, person);

        assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity).isInstanceOf(Person.class);
            softly.assertThat(((Person) entity).getId()).isEqualTo(1L);
        });

    }

    @Test
    @DisplayName("hasEntity 를 통해 Entity가 context 에 존재하는지 여부를 반환받을 수 있다.")
    void hasEntityTest() {
        persistenceContext.addEntity(personEntityKey, person);

        assertThat(persistenceContext.hasEntity(personEntityKey)).isTrue();
    }

    @Test
    @DisplayName("addEntityEntry 를 통해 EntityEntry 를 추가한 뒤 조회할 수 있다.")
    void addEntityEntryTest() {
        persistenceContext.addEntityEntry(personEntityKey, Status.LOADING);

        assertThat(persistenceContext.getEntityEntry(personEntityKey)).isNotEmpty();
    }

    @Test
    @DisplayName("updateEntityEntryStatus 를 통해 EntityEntry 의 상태를 변경할 수 있다.")
    void updateEntityEntryStatusTest() {
        persistenceContext.addEntityEntry(personEntityKey, Status.LOADING);

        persistenceContext.updateEntityEntryStatus(personEntityKey, Status.MANAGED);

        assertSoftly(softly -> {
            softly.assertThat(persistenceContext.getEntityEntry(personEntityKey)).isNotEmpty();
            final EntityEntry entityEntry = persistenceContext.getEntityEntry(personEntityKey).get();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.MANAGED);
        });
    }

}
