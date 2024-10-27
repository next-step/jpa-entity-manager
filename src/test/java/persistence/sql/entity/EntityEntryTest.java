package persistence.sql.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.config.TestPersistenceConfig;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.entity.data.Status;
import persistence.sql.fixture.TestPerson;
import persistence.sql.fixture.TestPersonNoGenerateValue;
import persistence.sql.loader.EntityLoader;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("EntityEntry 테스트")
class EntityEntryTest extends TestEntityInitialize {
    private EntityPersister entityPersister;
    private PersistenceContext persistenceContext;
    private EntityLoader<TestPerson> entityLoader;
    ;

    @BeforeEach
    void setup() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();
        entityPersister = config.entityPersister();
        persistenceContext = config.persistenceContext();
        entityLoader = EntityLoaderFactory.getInstance().getLoader(TestPerson.class);
    }

    @Test
    @DisplayName("newEntry 함수는 EntityEntry를 생성한다.")
    void testNewEntry() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);

        EntityEntry actual = EntityEntry.newEntry(entityPersister, persistenceContext, person, Status.MANAGED);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getEntity()).isEqualTo(person),
                () -> assertThat(actual.isDirty()).isFalse()
        );
    }

    @Test
    @DisplayName("newEntry 함수는 id가 없고 GeneratedValue 어노테이션이 있는 경우 id를 생성한다.")
    void testNewEntryWithGeneratedValue() {
        // given
        TestPerson person = new TestPerson( "catsbi", 55, "casbi@naver.com", 123);

        // when
        EntityEntry actual = EntityEntry.newEntry(entityPersister, persistenceContext, person, Status.SAVING);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getEntity()).isEqualTo(person),
                () -> assertThat(actual.isDirty()).isFalse()
        );
    }

    @Test
    @DisplayName("newEntry 함수는 id가 없고 GeneratedValue 어노테이션이 유효하지 않은 경우 예외를 던진다.")
    void testNewEntryWithInvalidGeneratedValue() {
        // given
        TestPersonNoGenerateValue person = new TestPersonNoGenerateValue("catsbi", 55, "casbi@naver.com", 123);

        // when, then
        assertThatThrownBy(() -> EntityEntry.newEntry(entityPersister, persistenceContext, person, Status.MANAGED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Primary key must not be null");
    }

    @Test
    @DisplayName("dirtyCheck 함수는 Status가 관리 상태이고, 엔티티가 변경된 경우 동기화 쿼리를 수행한다.")
    void testDirtyCheck() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityPersister.insert(person);
        EntityEntry personEntry = EntityEntry.newEntry(entityPersister, persistenceContext, person, Status.MANAGED);
        personEntry.dirtyCheck();

        // when
        person.setName("newName");
        personEntry.dirtyCheck();
        TestPerson loadedPerson = entityLoader.load(person.getId());

        //then
        assertThat(personEntry.isDirty()).isFalse();
        assertThat(loadedPerson.getName()).isEqualTo("newName");
    }

    @Test
    @DisplayName("dirtyCheck 함수는 Status가 미관리 상태이고, 엔티티가 변경된 경우 동기화 쿼리를 수행하지 않는다.")
    void testDirtyCheckNoManagedStatus() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityPersister.insert(person);
        EntityEntry personEntry = EntityEntry.newEntry(entityPersister, persistenceContext, person, Status.GONE);
        personEntry.dirtyCheck();

        // when
        person.setName("newName");
        personEntry.dirtyCheck();
        TestPerson loadedPerson = entityLoader.load(person.getId());

        //then
        assertThat(personEntry.isDirty()).isFalse();
        assertThat(loadedPerson.getName()).isEqualTo("catsbi");
    }

    @Test
    @DisplayName("dirtyCheck 함수는 Status가 삭제 상태일 경우 삭제 쿼리를 수행한다.")
    void testDirtyCheckDeleteStatus() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityPersister.insert(person);
        EntityEntry personEntry = EntityEntry.newEntry(entityPersister, persistenceContext, person, Status.DELETED);

        // when
        personEntry.dirtyCheck();
        TestPerson loadedPerson = entityLoader.load(person.getId());

        //then
        assertThat(loadedPerson).isNull();
    }
}
