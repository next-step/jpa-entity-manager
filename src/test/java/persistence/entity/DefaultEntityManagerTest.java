package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

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
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.NoAutoIncrementPerson;
import persistence.testFixtures.Person;

class DefaultEntityManagerTest {
    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;
    private Dialect dialect;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        dialect = new FakeDialect();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).create());
    }


    @Test
    @DisplayName("엔티티 매니저에 의해 저장 된다.")
    void persist() {
        //given
        Person person = new Person("이름", 19, "asd@gmail.com");
        Dialect dialect = new FakeDialect();
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()),
                dialect);

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
        Dialect dialect = new FakeDialect();
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()),
                dialect);
        entityManager.persist(person);

        //when
        final List<Person> persons = entityManager.findAll(Person.class);
        entityManager.remove(persons.get(0));
        final List<Person> resultPersons = entityManager.findAll(Person.class);

        //then
        assertThat(resultPersons).hasSize(0);
    }

    @Test
    @DisplayName("엔티티 매니저에 의해 하나의 데이터만 조회 된다.")
    void findById() {
        //given
        NoAutoIncrementPerson person = new NoAutoIncrementPerson(2L,"이름", 19, "asd@gmail.com");
        Dialect dialect = new FakeDialect();
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()), dialect);
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
        Person person = new Person("이름", 19, "asd@gmail.com");
        Dialect dialect = new FakeDialect();
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()), dialect);


        //when
        final Person person1 = entityManager.persist(person);
        final Person person2 = entityManager.persist(person);
        final Person person3 = entityManager.persist(person);


        //then
        assertThat(person1).isEqualTo(entityManager.find(Person.class, person1.getId()));
        assertThat(person2).isEqualTo(entityManager.find(Person.class, person2.getId()));
        assertThat(person3).isEqualTo(entityManager.find(Person.class, person3.getId()));
    }


    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        server.stop();
    }

}
