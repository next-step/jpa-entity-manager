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
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class DefaultEntityManagerTest {
    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;
    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.from(Person.class).create());
    }


    @Test
    @DisplayName("엔티티 매니저에 의해 저장 된다.")
    void persist() {
        //given
        Person person = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()));

        //when
        entityManager.persist(person);
        final List<Person> persons = entityManager.findAll(Person.class);

        //then
        assertSoftly((it) -> {
            it.assertThat(persons).hasSize(1);
            it.assertThat(persons.get(0).getAge()).isEqualTo(person.getAge());
            it.assertThat(persons.get(0).getName()).isEqualTo(person.getName());
            it.assertThat(persons.get(0).getEmail()).isEqualTo(person.getEmail());
            it.assertThat(persons.get(0).getId()).isNotNull();
        });
    }

    @Test
    @DisplayName("엔티티 매니저에 의해 삭제 된다.")
    void remove() {
        //given
        Person person = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()));
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
        Person person = new Person("이름", 19, "asd@gmail.com");
        EntityManager entityManager = new DefaultEntityManager(jdbcTemplate, new EntityMeta(person.getClass()));
        entityManager.persist(person);

        //when
        final Person savePerson = entityManager.findAll(Person.class).get(0);
        final Person findPerson = entityManager.find(Person.class, savePerson.getId());

        //then
        assertSoftly((it) -> {
            it.assertThat(savePerson.getAge()).isEqualTo(findPerson.getAge());
            it.assertThat(savePerson.getName()).isEqualTo(findPerson.getName());
            it.assertThat(savePerson.getEmail()).isEqualTo(findPerson.getEmail());
            it.assertThat(savePerson.getId()).isEqualTo(findPerson.getId());
        });
    }


    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.from(Person.class).drop());
        server.stop();
    }

}
