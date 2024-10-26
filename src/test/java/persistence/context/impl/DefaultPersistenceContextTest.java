package persistence.context.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.context.PersistenceContext;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.fixture.TestPerson;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultPersistenceContext 테스트")
class DefaultPersistenceContextTest extends TestEntityInitialize {
    private final PersistenceContext context = new DefaultPersistenceContext();


    @Test
    @DisplayName("get 함수는 저장된 엔티티를 반환한다.")
    void testGetWithEntity() {
        // given
        TestPerson entity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(entity.getId(), entity);

        // when
        TestPerson actual = context.get(TestPerson.class, entity.getId());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(entity);
    }


    @Test
    @DisplayName("get 함수는 유효하지 않은 식별자를 전달하면 null을 반환한다.")
    void testGetWithInvalidId() {
        // when
        TestPerson actual = context.get(TestPerson.class, 1L);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("isDirty 함수는 변경이 필요한 엔티티가 있을 경우 true를 반환한다.")
    void testIsDirtyWithDirtyEntity() {
        // given
        TestPerson catsbiEnity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        TestPerson crongEntity = new TestPerson(2L, "crong", 7, "crong@naver.com", 123);
        context.add(catsbiEnity.getId(), catsbiEnity);
        context.createDatabaseSnapshot(catsbiEnity.getId(), catsbiEnity);
        context.add(crongEntity.getId(), crongEntity);
        context.createDatabaseSnapshot(crongEntity.getId(), crongEntity);

        //when
        catsbiEnity.setName("newCatsbi");

        //then
        assertThat(context.isDirty()).isTrue();
    }

    @Test
    @DisplayName("isDirty 함수는 변경이 필요한 엔티티가 없을 경우 true를 반환한다.")
    void testIsDirtyWithoutDirtyEntity() {
        // given
        TestPerson catsbiEnity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(catsbiEnity.getId(), catsbiEnity);
        context.createDatabaseSnapshot(catsbiEnity.getId(), catsbiEnity);

        //then
        assertThat(context.isDirty()).isFalse();
    }

    @Test
    @DisplayName("isDirty 함수는 영속화 대상이지만 스냅샷이 없을 경우도 true를 반환한다.")
    void testIsDirtyWithoutSnapshot() {
        // given
        TestPerson catsbiEnity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(catsbiEnity.getId(), catsbiEnity);

        //then
        assertThat(context.isDirty()).isTrue();
    }

    @Test
    @DisplayName("getDirtyEntities 함수는 변경이 필요한 엔티티 목록을 반환한다.")
    void testGetDirtyEntities() {
        // given
        TestPerson catsbiEnity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        TestPerson crongEntity = new TestPerson(2L, "crong", 7, "crong@naver.com", 123);
        context.add(catsbiEnity.getId(), catsbiEnity);
        context.createDatabaseSnapshot(catsbiEnity.getId(), catsbiEnity);
        context.add(crongEntity.getId(), crongEntity);
        context.createDatabaseSnapshot(crongEntity.getId(), crongEntity);

        //when
        catsbiEnity.setName("newCatsbi");
        List<Object> actual = context.getDirtyEntities();

        //then
        assertThat(actual).containsExactly(catsbiEnity);
    }

    @Test
    @DisplayName("getDirtyEntities 함수는 변경이 필요한 엔티티가 없을 경우 빈 목록을 반환한다.")
    void testGetDirtyEntitiesWithoutDirtyEntity() {
        // given
        TestPerson catsbiEnity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        TestPerson crongEntity = new TestPerson(2L, "crong", 7, "crong@naver.com", 123);
        context.add(catsbiEnity.getId(), catsbiEnity);
        context.createDatabaseSnapshot(catsbiEnity.getId(), catsbiEnity);
        context.add(crongEntity.getId(), crongEntity);
        context.createDatabaseSnapshot(crongEntity.getId(), crongEntity);

        // when
        List<Object> actual = context.getDirtyEntities();

        // then
        assertThat(actual).isEmpty();
    }
}
