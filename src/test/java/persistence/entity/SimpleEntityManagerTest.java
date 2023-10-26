package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.IntegrationTestEnvironment;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class SimpleEntityManagerTest extends IntegrationTestEnvironment {

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        final EntityPersisterProvider entityPersisterProvider = new EntityPersisterProvider(dmlGenerator, jdbcTemplate);
        final EntityLoaderProvider entityLoaderProvider = new EntityLoaderProvider(dmlGenerator, jdbcTemplate);
        entityManager = new SimpleEntityManager(entityPersisterProvider, entityLoaderProvider);
    }

    @Test
    @DisplayName("entityManager.find 를 이용해 특정 객체를 DB 에서 조회할 수 있다.")
    void entityManagerFindTest() {
        final Person person = entityManager.find(Person.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(person).isNotNull();
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("test00");
            softly.assertThat(person.getAge()).isEqualTo(0);
            softly.assertThat(person.getEmail()).isEqualTo("test00@gmail.com");
        });
    }

    @Test
    @DisplayName("entityManager.persist 를 이용해 특정 객체를 DB 에 저장할 수 있다.")
    void entityManagerPersistTest() {
        final Person newPerson = new Person("min", 30, "jongmin4943@gmail.com");

        assertDoesNotThrow(() -> entityManager.persist(newPerson));
    }

    @Test
    @DisplayName("entityManager.remove 를 이용해 특정 객체를 DB 에서 삭제할 수 있다.")
    void entityManagerRemoveTest() {
        final Person newPerson = new Person("min", 30, "jongmin4943@gmail.com");
        entityManager.persist(newPerson);

        assertDoesNotThrow(() -> entityManager.remove(newPerson));
    }

}
