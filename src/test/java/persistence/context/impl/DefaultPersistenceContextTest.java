package persistence.context.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.config.PersistenceConfig;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.fixture.TestPerson;

import java.sql.SQLException;

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
    @DisplayName("get 함수는 저장된 엔티티를 반환한다.")
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
    @DisplayName("get 함수는 유효하지 않은 식별자를 전달하면 null을 반환한다.")
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
}
