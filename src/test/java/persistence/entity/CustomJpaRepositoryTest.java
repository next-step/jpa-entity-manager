package persistence.entity;

import domain.FixtureEntity.Person;
import jdbc.RowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.IntegrationTestEnvironment;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class CustomJpaRepositoryTest extends IntegrationTestEnvironment {

    private CustomJpaRepository<Person, Long> customJpaRepository;

    @BeforeEach
    void setup() {
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(new EntityScanner(Application.class), persistenceEnvironment);
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        customJpaRepository = new CustomJpaRepository<>(entityManager, Person.class);
    }

    @Test
    @DisplayName("customJpaRepository 를 통해 특정 Id 의 Entity를 조회할 수 있다.")
    void findByTest() {
        final Person saved = customJpaRepository.findById(1L);

        assertSoftly(softly -> {
            softly.assertThat(saved).isNotNull();
            softly.assertThat(saved.getId()).isEqualTo(1L);
        });
    }


    @Test
    @DisplayName("customJpaRepository 를 통해 Entity를 저장할 수 있다.")
    void saveTest() {
        final Person newPerson = new Person("newPerson", 1, "new@new.com");
        final Person saved = customJpaRepository.save(newPerson);

        assertSoftly(softly -> {
            softly.assertThat(saved.getId()).isNotNull();
            softly.assertThat(saved.getName()).isEqualTo("newPerson");
            softly.assertThat(saved.getAge()).isEqualTo(1);
            softly.assertThat(saved.getEmail()).isEqualTo("new@new.com");
        });
    }

    @Test
    @DisplayName("customJpaRepository 를 통해 기존 Entity를 수정할 수 있다.")
    void updateTest() {
        final Person fixture = customJpaRepository.findById(1L);

        fixture.changeEmail("something new");
        customJpaRepository.save(fixture);

        final Person testV2 = jdbcTemplate.queryForObject("select * from users where id=1", personRowMapper());
        assertSoftly(softly -> {
            softly.assertThat(fixture.getId()).isEqualTo(testV2.getId());
            softly.assertThat(fixture.getName()).isEqualTo(testV2.getName());
            softly.assertThat(fixture.getAge()).isEqualTo(testV2.getAge());
            softly.assertThat(fixture.getEmail()).isEqualTo(testV2.getEmail());
        });
    }

    private RowMapper<Person> personRowMapper() {
        return rs -> new Person(rs.getLong("id"), rs.getString("nick_name"), rs.getInt("old"), rs.getString("email"));
    }
}
