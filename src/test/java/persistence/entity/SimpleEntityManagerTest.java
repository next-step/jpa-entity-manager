package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.DifferentPerson;
import persistence.testFixtures.NoAutoIncrementPerson;
import persistence.testFixtures.Person;

class SimpleEntityManagerTest {
    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;
    private Dialect dialect;
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        dialect = new FakeDialect();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).create());
        jdbcTemplate.execute(QueryGenerator.of(DifferentPerson.class, dialect).create());
        entityManagerFactory = EntityManagerFactory.of("persistence.testFixtures", jdbcTemplate, dialect);
    }


    @Test
    @DisplayName("엔티티 매니저에 의해 저장 된다.")
    void persist() {
        //given
        Person person = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //when
        entityManager.persist(person);
        final List<Person> persons = entityManager.findAll(Person.class);

        //then
        assertThat(persons).hasSize(1);
    }

    @Test
    @DisplayName("엔티티 매니저에 의해 삭제 된다.")
    void remove() {
        //given
        Person person = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.persist(person);

        //when
        final List<Person> persons = entityManager.findAll(Person.class);
        entityManager.remove(persons.get(0));
        entityManager.flush();
        final List<Person> resultPersons = entityManager.findAll(Person.class);

        //then
        assertThat(resultPersons).hasSize(0);
    }

    @Test
    @DisplayName("엔티티 매니저에 의해 하나의 데이터만 조회 된다.")
    void findById() {
        //given
        NoAutoIncrementPerson person = new NoAutoIncrementPerson(2L, "이름", 19, "asd@gmail.com");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.persist(person);

        //when
        final NoAutoIncrementPerson savePerson = entityManager.find(NoAutoIncrementPerson.class, 2L);

        //then
        assertThat(savePerson).isEqualTo(person);
    }

    @Test
    @DisplayName("1차 캐시에 의해 조회가 된다.")
    void firstCache() {
        //given
        Person person1 = new Person("이름", 19, "asd@gmail.com");
        Person person2 = new Person("이름", 19, "asd@gmail.com");
        Person person3 = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //when
        final Person savePerson1 = entityManager.persist(person1);
        final Person savePerson2= entityManager.persist(person2);
        final Person savePerson3 = entityManager.persist(person3);

        //then
        assertSoftly(it -> {
                    it.assertThat(savePerson1).isEqualTo(entityManager.find(Person.class, person1.getId()));
                    it.assertThat(savePerson2).isEqualTo(entityManager.find(Person.class, person2.getId()));
                    it.assertThat(savePerson3).isEqualTo(entityManager.find(Person.class, person3.getId()));
                }
        );
    }

    @Test
    @DisplayName("엔티티의 상태를 변경한 대상이 데이터가 업데이트가 된다")
    void dirtyCheck() {
        //given
        final String CHANGE_EMAIL_STRING = "change23@gmail.com";
        Person person = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        final Person person1 = entityManager.persist(person);
        final Person person2 = entityManager.persist(person);
        final Person person3 = entityManager.persist(person);

        //when
        person1.changeEmail(CHANGE_EMAIL_STRING);
        person2.changeEmail(CHANGE_EMAIL_STRING);
        entityManager.flush();

        final Person findPerson1 = entityManager.find(Person.class, person1.getId());
        final Person findPerson2 = entityManager.find(Person.class, person2.getId());
        final Person findPerson3 = entityManager.find(Person.class, person3.getId());

        //then
        assertSoftly(it -> {
            it.assertThat(findPerson1).isEqualTo(person1);
            it.assertThat(findPerson1.getEmail()).isEqualTo(CHANGE_EMAIL_STRING);
            it.assertThat(findPerson2.getEmail()).isEqualTo(CHANGE_EMAIL_STRING);
            it.assertThat(findPerson3.getEmail()).isNotEqualTo(CHANGE_EMAIL_STRING);
        });
    }

    @Test
    @DisplayName("하나의 엔티티 매니저에 여러 엔터티가 저장 된다.")
    void multiDomainPersist() {
        //given
        Person person = new Person("이름", 19, "asd@gmail.com");
        DifferentPerson noAutoIncrementPerson = new DifferentPerson(2L, "이름", 19, "asd@gmail.com");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //when
        entityManager.persist(person);
        entityManager.persist(noAutoIncrementPerson);
        final List<Person> persons = entityManager.findAll(Person.class);
        final List<DifferentPerson> differentPerson = entityManager.findAll(DifferentPerson.class);

        //then
        assertSoftly(it -> {
            it.assertThat(persons).hasSize(1);
            it.assertThat(differentPerson).hasSize(1);
        });
    }


    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        jdbcTemplate.execute(QueryGenerator.of(DifferentPerson.class, dialect).drop());
        server.stop();
    }

}
