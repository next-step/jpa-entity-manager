package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

import database.DatabaseServer;
import database.H2;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import jdbc.JdbcTemplate;
import org.assertj.core.api.SoftAssertions;
import org.h2.jdbc.JdbcResultSet;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class EntityLoaderTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;
    private Dialect dialect;

    @BeforeEach
    void setUp() throws Exception {
        server = new H2();
        server.start();
        dialect = new FakeDialect();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).create());
    }



    @Test
    @DisplayName("데이터를 전체를 조회하고 엔티티에 맵핑한다")
    void findAll() {
        //given
        EntityMeta entityMeta = new EntityMeta(Person.class);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, entityMeta, dialect);
        QueryGenerator<Person> q = QueryGenerator.of(Person.class, dialect);
        Person person = new Person("이름", 19, "asd@gmail.com");
        jdbcTemplate.execute(q.insert(person));

        //when
        final List<Person> personList = entityLoader.findAll(Person.class);
        Person firstPerson = personList.get(0);
        final Person findPerson = entityLoader.find(Person.class, firstPerson.getId());

        //then
        assertSoftly((it) -> {
            it.assertThat(personList).hasSize(1);
            it.assertThat(firstPerson.getName()).isEqualTo(findPerson.getName());
            it.assertThat(firstPerson.getAge()).isEqualTo(findPerson.getAge());
            it.assertThat(firstPerson.getEmail()).isEqualTo(findPerson.getEmail());
        });
    }

    @Test
    @DisplayName("데이터를 조회하고 엔티티에 맵핑한다")
    void find() {
        //given
        EntityMeta entityMeta = new EntityMeta(Person.class);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, entityMeta, dialect);
        QueryGenerator<Person> q = QueryGenerator.of(Person.class, dialect);
        Person person = new Person("이름", 19, "asd@gmail.com");
        Person person2 = new Person("이름", 19, "asd@gmail.com");
        jdbcTemplate.execute(q.insert(person));
        jdbcTemplate.execute(q.insert(person));

        //when
        final List<Person> personList = entityLoader.findAll(Person.class);

        //then
        assertSoftly((it) -> {
            it.assertThat(personList).hasSize(2);
            it.assertThat(personList.get(0).getName()).isEqualTo(person.getName());
            it.assertThat(personList.get(0).getAge()).isEqualTo(person.getAge());
            it.assertThat(personList.get(0).getEmail()).isEqualTo(person.getEmail());
            it.assertThat(personList.get(1).getName()).isEqualTo(person2.getName());
            it.assertThat(personList.get(1).getAge()).isEqualTo(person2.getAge());
            it.assertThat(personList.get(1).getEmail()).isEqualTo(person2.getEmail());
        });
    }

    @Test
    @DisplayName("없는 데이터를 조회하면 null을 반환한다")
    void test() {
        EntityMeta entityMeta = new EntityMeta(Person.class);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, entityMeta, dialect);

        Person person = entityLoader.find(Person.class, 1L);

        assertThat(person).isNull();
    }

    @AfterEach
    void cleanUp() throws Exception {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
    }

}
