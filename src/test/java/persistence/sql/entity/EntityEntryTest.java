package persistence.sql.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.sql.context.KeyHolder;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.entity.data.Status;
import persistence.sql.fixture.TestPerson;
import persistence.sql.fixture.TestPersonNoGenerateValue;
import persistence.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("EntityEntry 테스트")
class EntityEntryTest extends TestEntityInitialize {

    @Test
    @DisplayName("newEntry 함수는 EntityEntry를 생성한다.")
    void testNewEntry() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);

        EntityEntry actual = EntityEntry.newEntry(person, Status.MANAGED);

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
        assertThatThrownBy(() -> EntityEntry.newEntry(person, Status.MANAGED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Primary key must not be null");
    }

    @Test
    @DisplayName("newLoadingEntry 함수는 상태가 LOADING 상태인 EntityEntry를 생성한다.")
    void testNewLoadingEntry() {
        // given
        Long primaryKey = 1L;

        EntityEntry actual = EntityEntry.newLoadingEntry(primaryKey, TestPerson.class);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getStatus()).isEqualTo(Status.LOADING),
                () -> assertThat(actual.getEntity()).isNull(),
                () -> assertThat(actual.getSnapshot()).isNull()
        );
    }


    @Test
    @DisplayName("synchrnoizingSnapshot 함수는 Entity의 스냅샷을 업데이트한다.")
    void testSynchronizingSnapshot() {
        // given
        String expectedName = "newCatsbi";
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newEntry(person, Status.MANAGED);

        // when
        person.setName(expectedName);
        entry.synchronizingSnapshot();

        Object snapshot = entry.getSnapshot();
        Object actual = ReflectionUtils.getFieldValue(snapshot, "name");

        // then
        assertThat(actual).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("synchrnoizingSnapshot 함수는 식별자나 Transient 애노테이션 필드의 변경사항은 반영하지 않는다.")
    void testSynchronizingSnapshotIdAndTransient () {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newEntry(person, Status.MANAGED);

        // when
        person.setId(2L);
        person.setIndex(456);
        entry.synchronizingSnapshot();

        Object snapshot = entry.getSnapshot();
        Object actualId = ReflectionUtils.getFieldValue(snapshot, "id");
        Object actualIndex = ReflectionUtils.getFieldValue(snapshot, "index");


        // then
        assertThat(actualId).isEqualTo(1L);
        assertThat(actualIndex).isNull();
    }

    @Test
    @DisplayName("updateEntity 함수는 Entity를 업데이트하고 snapshot이 null일경우 생성한다.")
    void testUpdateEntityAndSnapshotIsNull() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newLoadingEntry(1L, TestPerson.class);

        //when
        entry.updateEntity(person);

        //then
        assertAll(
                () -> assertThat(entry.getEntity()).isEqualTo(person),
                () -> assertThat(entry.getSnapshot()).isNotNull()
        );
    }


    @Test
    @DisplayName("isDirty 함수는 변경이 필요한 엔티티가 있을 경우 true를 반환한다.")
    void testIsDirtyWithDirtyEntity() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newEntry(person, Status.MANAGED);

        // when
        person.setName("newCatsbi");

        // then
        assertThat(entry.isDirty()).isTrue();
    }

    @Test
    @DisplayName("isDirty 함수는 변경이 필요한 엔티티가 없을 경우 false를 반환한다.")
    void testIsDirtyWithNotDirtyEntity() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newEntry(person, Status.MANAGED);

        // when
        person.setIndex(456);

        // then
        assertThat(entry.isDirty()).isFalse();
    }

    @Test
    @DisplayName("isDirty 함수는 관리되지 않는 상태일 경우 false를 반환한다.")
    void testIsDirtyWithNotManagedStatus() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newEntry(person, Status.MANAGED);

        // when
        person.setName("newCatsbi");
        entry.updateStatus(Status.GONE);

        // then
        assertThat(entry.isDirty()).isFalse();
    }

    @ParameterizedTest
    @DisplayName("isDirty 함수는 entity나 snapshot이 null인 경우 true를 반환한다.")
    @MethodSource("provideNullEntityOrSnapshotEntry")
    void testIsDirtyWithNullEntityOrSnapshot(EntityEntry actualEntry) {

        // when
        boolean actual = actualEntry.isDirty();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("isDirty 함수는 entity와 snapshot이 동일할 경우 false를 반환한다.")
    void testIsDirtyEqualsEntityAndSnapshot() {
        // given
        TestPerson person = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        EntityEntry entry = EntityEntry.newEntry(person, Status.MANAGED);

        // when
        boolean actual = entry.isDirty();

        // then
        assertThat(actual).isFalse();

    }


    public static Stream<Arguments> provideNullEntityOrSnapshotEntry() {
        EntityEntry entityNullEntry = createDummyEntry();
        entityNullEntry.updateEntity(null);
        EntityEntry snapshotNullEntry = createDummyEntry();
        ReflectionUtils.setFieldValue(snapshotNullEntry, "snapshot", null);

        return Stream.of(
                Arguments.of(entityNullEntry),
                Arguments.of(snapshotNullEntry)
        );
    }

    private static EntityEntry createDummyEntry() {
        TestPerson catsbi = new TestPerson(1L, "catsbi", 55, "casbi@naver.com", 123);
        SimpleMetadataLoader<TestPerson> loader = new SimpleMetadataLoader<>(TestPerson.class);
        Object snapshot = createSnapshot(catsbi, loader);

        return new EntityEntry(loader, Status.MANAGED, catsbi, snapshot, new KeyHolder(TestPerson.class, catsbi.getId()));
    }

    private static <T> T createSnapshot(Object source, MetadataLoader<?> loader) {
        try {

            Object snapshotEntity = loader.getNoArgConstructor().newInstance();
            for (int i = 0; i < loader.getColumnCount(); i++) {
                Field field = loader.getField(i);
                field.setAccessible(true);
                field.set(snapshotEntity, field.get(source));
            }

            return (T) snapshotEntity;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create snapshot entity");
        }
    }
}
