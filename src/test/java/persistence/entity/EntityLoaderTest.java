package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
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
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        EntityMapper entityMapper = new EntityMapper(entityMeta);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, queryGenerator, entityMapper);

        Person person = new Person("이름", 19, "asd@gmail.com");
        jdbcTemplate.execute(queryGenerator.insert().build(person));

        //when
        final List<Person> personList = entityLoader.findAll(Person.class);
        Person firstPerson = personList.get(0);
        final Person findPerson = entityLoader.find(Person.class, firstPerson.getId());

        //then
        assertThat(firstPerson).isEqualTo(findPerson);
    }

    @Test
    @DisplayName("데이터를 조회하고 엔티티에 맵핑한다")
    void find() {
        //given
        QueryGenerator queryGenerator = QueryGenerator.of(Person.class, dialect);
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, queryGenerator,
                new EntityMapper(EntityMeta.from(Person.class)));

        Person person = new Person("이름", 19, "asd@gmail.com");
        Person person2 = new Person("이름", 19, "asd@gmail.com");
        jdbcTemplate.execute(queryGenerator.insert().build(person));
        jdbcTemplate.execute(queryGenerator.insert().build(person2));

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
    void noDataIsNull() {
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, QueryGenerator.of(Person.class, dialect),
                new EntityMapper(EntityMeta.from(Person.class)));

        Person person = entityLoader.find(Person.class, 1L);

        assertThat(person).isNull();
    }

    @Test
    @DisplayName("데이터를 저장하고 키값을 가져온다.")
    void save() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);

        //when
        Person person = new Person("이름", 19, "asd");
        final Long l = jdbcTemplate.insertForGenerateKey(queryGenerator.insert().build(person));
        final Long l2 = jdbcTemplate.insertForGenerateKey(queryGenerator.insert().build(person));
        final Long l3 = jdbcTemplate.insertForGenerateKey(queryGenerator.insert().build(person));

        //then
        assertSoftly((it) -> {
            it.assertThat(l).isEqualTo(1L);
            it.assertThat(l2).isEqualTo(2L);
            it.assertThat(l3).isEqualTo(3L);
        });
    }

    @AfterEach
    void cleanUp() throws Exception {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
    }

}
