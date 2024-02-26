package testsupport;

import database.sql.Person;
import database.sql.dml.SelectQueryBuilder;
import jdbc.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EntityTestUtils {
    private EntityTestUtils() {
    }

    public static Person newPerson(Long id, String name, int age, String email) {
        return new Person(id, name, age, email);
    }

    public static void assertSamePerson(Person actual, Person expected, boolean compareIdField) {
        assertAll(
                () -> {
                    if (compareIdField) assertThat(actual.getId()).isEqualTo(expected.getId());
                },
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getAge()).isEqualTo(expected.getAge()),
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()));
    }

    public static List<Person> findPeople(JdbcTemplate jdbcTemplate) {
        String query = new SelectQueryBuilder(Person.class).buildQuery();
        return jdbcTemplate.query(query, resultSet -> new Person(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email")));
    }

    public static Long getLastSavedId(JdbcTemplate jdbcTemplate1) {
        List<Person> people = findPeople(jdbcTemplate1);
        if (people.isEmpty()) return null;
        return people.get(people.size() - 1).getId();
    }
}
