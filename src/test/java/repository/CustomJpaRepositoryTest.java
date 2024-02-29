package repository;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.MyEntityManager;
import persistence.sql.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.domain.dialect.H2Dialect;
import persistence.support.DatabaseSetup;

import static org.assertj.core.api.Assertions.assertThat;

@DatabaseSetup
class CustomJpaRepositoryTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect());
        String createQuery = createQueryBuilder.build(Person.class);
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown(JdbcTemplate jdbcTemplate) {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(new H2Dialect());
        String dropQuery = dropQueryBuilder.build(Person.class);
        jdbcTemplate.execute(dropQuery);
    }

    @Test
    @DisplayName("save 메서드는 값을 저장한다.")
    void save_persist() {
        //given
        EntityManager entityManager = new MyEntityManager(jdbcTemplate);
        CustomJpaRepository<Person, Long> repository = new CustomJpaRepository<Person, Long>(entityManager);
        Person person = new Person(null, "ABC", 20, "email.com", 10);

        //when
        repository.save(person);

        //then
        Person actual = entityManager.find(Person.class, 1L);
        assertThat(actual).isEqualTo(person);
    }

    @Test
    @DisplayName("save 메서드는 영속화된 엔티티의 경우 값을 수정한다.")
    void save_merge() {
        //given
        EntityManager entityManager = new MyEntityManager(jdbcTemplate);
        CustomJpaRepository<Person, Long> repository = new CustomJpaRepository<Person, Long>(entityManager);
        Person person = new Person(null, "ABC", 20, "email.com", 10);
        repository.save(person);

        //when
        Person updatePerson = new Person(1L, "QWE", 10, "update.com", 10);
        repository.save(updatePerson);

        //then
        Person actual = entityManager.find(Person.class, 1L);
        assertThat(actual).isEqualTo(updatePerson);
    }
}
