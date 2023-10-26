package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.IntegrationTestEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("Id 가 없는 Entity 를 Persist 시 Id 가 주입되어있다.")
    void persistTest() {
        final Person person = new Person("test", 30, "test@test.com");
        entityManager.persist(person);

        assertThat(person.getId()).isNotNull();
    }

    @Test
    @DisplayName("같은 Id 로 Entity 를 find 시 동일한 객체가 반환된다.")
    void findIdentityTest() {
        final Person person1 = entityManager.find(Person.class, 1L);
        final Person person2 = entityManager.find(Person.class, 1L);

        assertThat(person1 == person2).isTrue();
    }

    @Test
    @DisplayName("Entity 를 persist 하고 같은 아이디로 find 시 동일한 객체가 반환된다.")
    void findIdentityFromPersistTest() {
        final Person person1 = new Person("test", 30, "test@test.com");
        entityManager.persist(person1);
        final Person person2 = entityManager.find(Person.class, person1.getId());

        assertThat(person1 == person2).isTrue();
    }

    @Test
    @DisplayName("Entity 를 변경 후 persist 하면 update 된다.")
    void updatePersistTest() {
        final Person test = entityManager.find(Person.class, 1L);

        test.changeEmail("changed!");
        entityManager.persist(test);

        final Person testV2 = jdbcTemplate.queryForObject("select * from users where id=1", personRowMapper());
        assertSoftly(softly -> {
            softly.assertThat(test.getId()).isEqualTo(testV2.getId());
            softly.assertThat(test.getName()).isEqualTo(testV2.getName());
            softly.assertThat(test.getAge()).isEqualTo(testV2.getAge());
            softly.assertThat(test.getEmail()).isEqualTo(testV2.getEmail());
        });
    }

    @Test
    @DisplayName("Id 를 가진 객체를 persist 할 경우 update 된다.")
    void updatePersistFailTest() {
        final Person test = new Person(1L, "changedTester", 111, "changedEmail");

        entityManager.persist(test);

        final Person testV2 = jdbcTemplate.queryForObject("select * from users where id=1", personRowMapper());
        assertSoftly(softly -> {
            softly.assertThat(test.getId()).isEqualTo(testV2.getId());
            softly.assertThat(test.getName()).isEqualTo(testV2.getName());
            softly.assertThat(test.getAge()).isEqualTo(testV2.getAge());
            softly.assertThat(test.getEmail()).isEqualTo(testV2.getEmail());
        });
    }

}
