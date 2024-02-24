package persistence.entity;

import database.H2;
import database.sql.Person;
import database.sql.ddl.CreateQueryBuilder;
import database.sql.util.type.MySQLTypeConverter;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static persistence.entity.EntityTestUtils.*;

class EntityPersisterTest {
    private static final MySQLTypeConverter typeConverter = new MySQLTypeConverter();

    private static H2 server;
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute("DROP TABLE users IF EXISTS");
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class, typeConverter).buildQuery());
        entityPersister = new EntityPersister(jdbcTemplate, Person.class);
    }

    @Test
    void insert() {
        // row 두개를 추가하면
        Person person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);
        Person person2 = newPerson(null, "another name", 22, "another@name.com");
        entityPersister.insert(person2);

        // 잘 들어가있어야 한다
        List<Person> people = findPeople(jdbcTemplate);
        assertSamePerson(people.get(0), person, false);
        assertThat(people.get(0).getId()).isNotZero();
        assertSamePerson(people.get(1), person2, false);
        assertThat(people.get(1).getId()).isNotZero();
    }

    @Test
    void update() {
        // row 한 개를 삽입하고,
        Person person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 동일한 id 의 Person 객체로 update 한 후
        Long savedId = getLastSavedId(jdbcTemplate);
        Person personUpdating = newPerson(savedId, "updated name", 20, "updated@email.com");
        boolean res = entityPersister.update(personUpdating);

        // 남아있는 한개의 row 가 잘 업데이트돼야 한다
        assertThat(res).isTrue();
        List<Person> people = findPeople(jdbcTemplate);
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
        Person person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 그 row 를 삭제하고
        Long savedId = getLastSavedId(jdbcTemplate);
        entityPersister.delete(newPerson(savedId, "aaaa", 100, "bbbb@ccc.com"));

        // 개수를 세면 0개여야 한다.
        List<Person> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(0);
    }

}
