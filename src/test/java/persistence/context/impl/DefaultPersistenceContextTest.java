package persistence.context.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.config.PersistenceConfig;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.fixture.TestPerson;
import persistence.sql.loader.EntityLoader;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultPersistenceContext 테스트")
class DefaultPersistenceContextTest extends TestEntityInitialize {
    private PersistenceContext context;
    private EntityPersister entityPersister;

    @BeforeEach
    void setup() throws SQLException {
        PersistenceConfig config = PersistenceConfig.getInstance();

        context = config.persistenceContext();
        entityPersister = config.entityPersister();
    }

    @Test
    @DisplayName("addEntry 함수는 Status가 Saving이면 Entity를 insert하고 EntityEntry를 생성한다.")
    void testAddEntry() {
        // given
        TestPerson catsbiEntity = new TestPerson("catsbi", 33, "catsbi@naver.com", 123);
        entityPersister.insert(catsbiEntity);
        context.addEntry(catsbiEntity, Status.SAVING, entityPersister);

        TestPerson actual = EntityLoaderFactory.getInstance().getLoader(TestPerson.class).load(catsbiEntity.getId());

        assertThat(context.getEntry(TestPerson.class, catsbiEntity.getId())).isNotNull();
        assertThat(actual.getName()).isEqualTo("catsbi");
    }

    @Test
    @DisplayName("getEntry 함수는 저장된 엔티티를 반환한다.")
    void testGetEntryWithEntity() {
        // given
        TestPerson entity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.addEntry(entity, Status.MANAGED, entityPersister);

        // when
        EntityEntry actual = context.getEntry(TestPerson.class, entity.getId());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getEntity()).isEqualTo(entity);
    }


    @Test
    @DisplayName("getEntry 함수는 유효하지 않은 식별자를 전달하면 null을 반환한다.")
    void testGetEntryWithInvalidId() {
        // when
        EntityEntry actual = context.getEntry(TestPerson.class, 1L);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("isDirty 함수는 변경이 필요한 엔티티가 있을 경우 true를 반환한다.")
    void testIsDirtyWithDirtyEntity() {
        // given
        TestPerson catsbiEnity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        TestPerson crongEntity = new TestPerson(2L, "crong", 7, "crong@naver.com", 123);
        EntityEntry catsbiEntry = context.addEntry(catsbiEnity, Status.MANAGED, entityPersister);
        EntityEntry crongEntry = context.addEntry(crongEntity, Status.MANAGED, entityPersister);

        //when
        catsbiEnity.setName("newCatsbi");

        //then
        assertThat(catsbiEntry.isDirty()).isTrue();
        assertThat(crongEntry.isDirty()).isFalse();
    }

    @Test
    @DisplayName("dirtyCheck 함수는 저장이 필요한 엔티티를 동기화한다.")
    void testDirtyCheckWithValidEntries() {
        // given
        EntityLoader<TestPerson> loader = EntityLoaderFactory.getInstance().getLoader(TestPerson.class);
        TestPerson catsbiEntity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        EntityEntry entityEntry = new EntityEntry(loader.getMetadataLoader(),
                Status.SAVING,
                catsbiEntity,
                null,
                new KeyHolder(TestPerson.class, catsbiEntity.getId()));
        Map<KeyHolder, EntityEntry> entryMap = ReflectionUtils.getFieldValue(context, "context");
        entryMap.put(entityEntry.getKey(), entityEntry);

        //when
        context.dirtyCheck(entityPersister);
        TestPerson actual = EntityLoaderFactory.getInstance().getLoader(TestPerson.class).load(catsbiEntity.getId());

        assertThat(context.getEntry(TestPerson.class, catsbiEntity.getId())).isNotNull();
        assertThat(actual.getName()).isEqualTo("catsbi");
    }

    @Test
    @DisplayName("dirtyCheck 함수는 변경이 필요한 엔티티를 동기화한다.")
    void testDirtyCheckWithDirtyEntity() {
        // given
        EntityLoader<TestPerson> loader = EntityLoaderFactory.getInstance().getLoader(TestPerson.class);
        TestPerson catsbiEntity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.addEntry(catsbiEntity, Status.SAVING, entityPersister);

        // when
        catsbiEntity.setName("newCatsbi");
        context.dirtyCheck(entityPersister);

        // then
        TestPerson actual = loader.load(catsbiEntity.getId());
        assertThat(actual.getName()).isEqualTo("newCatsbi");
    }

    @Test
    @DisplayName("deleteEntry 함수는 저장된 엔티티를 삭제한다.")
    void testDeleteEntry() {
        // given
        EntityLoader<TestPerson> loader = EntityLoaderFactory.getInstance().getLoader(TestPerson.class);
        TestPerson catsbiEntity = new TestPerson( "catsbi", 33, "catsbi@naver.com", 123);
        entityPersister.insert(catsbiEntity);
        EntityEntry entityEntry = context.addEntry(catsbiEntity, Status.MANAGED, entityPersister);

        // when
        entityEntry.updateStatus(Status.DELETED);
        context.dirtyCheck(entityPersister);
        TestPerson actual = loader.load(catsbiEntity.getId());

        // then
        assertThat(actual).isNull();

    }
}
