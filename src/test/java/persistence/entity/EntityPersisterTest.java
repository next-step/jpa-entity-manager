package persistence.entity;

import database.DatabaseServer;
import database.H2;
import database.sql.Person;
import database.sql.ddl.CreateQueryBuilder;
import database.sql.dml.SelectQueryBuilder;
import database.sql.util.type.MySQLTypeConverter;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityPersisterTest {
    private static final MySQLTypeConverter typeConverter = new MySQLTypeConverter();

    private static JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;

    @BeforeAll
    static void beforeAll() throws SQLException {
        DatabaseServer server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class, typeConverter).buildQuery());
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users");
        entityPersister = new EntityPersister(jdbcTemplate, Person.class);
    }

    @Test
    void insert() {
        // row 두개를 추가하면
        Person person = newPerson(0L, "some name", 11, "some@name.com");
        entityPersister.insert(person);
        Person person2 = newPerson(0L, "another name", 22, "another@name.com");
        entityPersister.insert(person2);

        // 잘 들어가있어야 한다
        List<Person> people = findPeople();
        assertSamePerson(people.get(0), person, false);
        assertThat(people.get(0).getId()).isNotZero();
        assertSamePerson(people.get(1), person2, false);
        assertThat(people.get(1).getId()).isNotZero();
    }

    @Test
    void update() {
        // row 한 개를 삽입하고,
        Person person = newPerson(0L, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 동일한 id 의 Person 객체로 update 한 후
        Long savedId = findPeople().get(0).getId();
        Person personUpdating = newPerson(savedId, "updated name", 20, "updated@email.com");
        boolean res = entityPersister.update(personUpdating);

        // 남아있는 한개의 row 가 잘 업데이트돼야 한다
        assertThat(res).isTrue();
        List<Person> people = findPeople();
        assertThat(people).hasSize(1);
        Person found = people.get(0);
        assertSamePerson(found, personUpdating, true);
    }

//    @Test
//    void updateReturningFalseWhenMissingRecord() {
//        Person person = newPerson(1L, "some name", 11, "some@name.com");
//        boolean res = entityPersister.update(person);
//
//        assertThat(res).isFalse();
//    }

    @Test
    void delete() {
        // row 한 개를 저장 후에
        Person person = newPerson(0L, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 그 row 를 삭제하고
        Long savedId = findPeople().get(0).getId();
        entityPersister.delete(newPerson(savedId, "aaaa", 100, "bbbb@ccc.com"));

        // 개수를 세면 0개여야 한다.
        List<Person> people = findPeople();
        assertThat(people).hasSize(0);
    }

    private static List<Person> findPeople() {
        String query = new SelectQueryBuilder(Person.class).buildQuery();
        return jdbcTemplate.query(query, resultSet -> new Person(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email")));
    }

    private Person newPerson(long id, String name, int age, String email) {
        Person person = new Person();
        person.setIdForTesting(id);
        person.setName(name);
        person.setAge(age);
        person.setEmail(email);
        return person;
    }

    private static void assertSamePerson(Person actual, Person expected, boolean compareIdField) {
        assertAll(
                () -> {
                    if (compareIdField) assertThat(actual.getId()).isEqualTo(expected.getId());
                },
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getAge()).isEqualTo(expected.getAge()),
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()));
    }
}
