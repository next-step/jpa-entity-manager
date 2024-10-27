package persistence.sql.dml.impl;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.config.TestPersistenceConfig;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.fixture.TestPerson;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DefaultEntityManager 테스트")
class DefaultEntityManagerTest extends TestEntityInitialize {
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();
        entityManager = config.entityManager();
    }

    @Test
    @DisplayName("find 함수는 식별자가 유효한 경우 적절한 엔티티를 조회한다.")
    void testFind() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);
        entityManager.persist(person);

        // when
        TestPerson actual = entityManager.find(TestPerson.class, 1L);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("catsbi"),
                () -> assertThat(actual.getAge()).isEqualTo(55),
                () -> assertThat(actual.getEmail()).isEqualTo("catsbi@naver.com"),
                () -> assertThat(actual.getIndex()).isEqualTo(123)
        );
    }

    @Test
    @DisplayName("find 함수는 식별자가 유효하지 않은 경우 null을 반환한다.")
    void testFindWithInvalidId() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);
        entityManager.persist(person);

        // when
        TestPerson actual = entityManager.find(TestPerson.class, 999L);
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("find 함수는 식별자를 전달하지 않을 경우 예외를 던진다.")
    void testFindWithNullId() {
        // when, then
        assertThatThrownBy(() -> entityManager.find(TestPerson.class, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Primary key must not be null");
    }

    @Test
    @DisplayName("persist 함수는 엔티티를 저장한다.")
    void testPersist() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);

        // when
        entityManager.persist(person);

        // then
        TestPerson actual = entityManager.find(TestPerson.class, 1L);

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("persist 함수는 엔티티 매개변수를 전달하지 않을 경우 예외를 던진다.")
    void testPersistWithNullEntity() {
        // when, then
        assertThatThrownBy(() -> entityManager.persist(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null");
    }

    @Test
    @DisplayName("persist 함수는 엔티티가 이미 존재하는 경우 예외를 던진다..")
    void testPersistWithMerge() {
        // given
        TestPerson person = new TestPerson("catsbi", 25, "casbi@naver.com", 123);

        // when
        entityManager.persist(person);
        TestPerson actual = entityManager.find(TestPerson.class, 1L);

        actual.setName("newCatsbi");
        actual.setAge(123);
        assertThatThrownBy(() -> entityManager.persist(actual))
                .isInstanceOf(EntityExistsException.class)
                .hasMessage("Entity already exists");
    }

    @Test
    @DisplayName("merge 함수는 식별자가 이미 존재하는 Row의 식별자인 경우 엔티티를 병합한다.")
    void testMerge() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityManager.persist(person);

        // when
        TestPerson newPerson = new TestPerson(1L, "hansol", 33, "hansol@naver.com", 123);
        entityManager.merge(newPerson);

        // then
        TestPerson mergedPerson = entityManager.find(TestPerson.class, 1L);
        assertAll(
                () -> assertThat(mergedPerson).isNotNull(),
                () -> assertThat(mergedPerson.getName()).isEqualTo("hansol"),
                () -> assertThat(mergedPerson.getAge()).isEqualTo(33),
                () -> assertThat(mergedPerson.getEmail()).isEqualTo("hansol@naver.com")

        );
    }

    @Test
    @DisplayName("merge 함수는 식별자가 존재하지 않는 Row의 식별자인 경우 엔티티를 저장한다.")
    void testMergeWithNewEntity() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityManager.persist(person);

        TestPerson foundPerson = entityManager.find(TestPerson.class, 1L);
        foundPerson.setId(2L);
        entityManager.merge(foundPerson);

        List<TestPerson> foundPersons = entityManager.findAll(TestPerson.class);
        assertThat(foundPersons).hasSize(2);
    }

    @Test
    @DisplayName("remove 함수는 엔티티를 삭제한다.")
    void testRemove() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);

        // when
        entityManager.persist(person);
        List<TestPerson> persons = entityManager.findAll(TestPerson.class);

        assertThat(persons).hasSize(1);

        entityManager.remove(persons.getFirst());

        persons = entityManager.findAll(TestPerson.class);
        assertThat(persons).isEmpty();
    }

    @Test
    @DisplayName("transaction 상태를 on으로 바꾼 경우 dirty checking이 활성화된다.")
    void testDirtyChecking() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "casbi@naver.com", 123);
        entityManager.persist(person);

        // when
        entityManager.getTransaction().begin();
        TestPerson foundPerson = entityManager.find(TestPerson.class, 1L);
        foundPerson.setName("newCatsbi");
        entityManager.getTransaction().commit();

        //given
        TestPerson loadedEntity = entityManager.find(TestPerson.class, 1L);
        assertThat(loadedEntity.getName()).isEqualTo("newCatsbi");
    }
}
