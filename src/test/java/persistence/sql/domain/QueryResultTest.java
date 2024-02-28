package persistence.sql.domain;

import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.Test;
import persistence.sql.entity.Person;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class QueryResultTest {

    private final Class<Person> clazz = Person.class;

    @Test
    void should_return_single_entity() {
        DatabaseTable table = new DatabaseTable(Person.class);
        QueryResult<Person> queryResult = new QueryResult<>(table, Person.class);
        Person person = queryResult.mapRow(new MockResultSet());


        assertAll(
                () -> validateFieldValue("id", 1l, person),
                () -> validateFieldValue("name", "cs", person),
                () -> validateFieldValue("age", 29, person),
                () -> validateFieldValue("email", "katd216@gmail.com", person)
        );
    }

    private void validateFieldValue(String fieldName, Object fieldValue, Object instance) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        assertThat(field.get(instance)).isEqualTo(fieldValue);
    }

    private static class MockResultSet extends SimpleResultSet implements ResultSet {

        private final Map<String, Object> rows = Map.of(
                "id", 1l,
                "nick_name", "cs",
                "old", 29,
                "email", "katd216@gmail.com"
        );

        @Override
        public Object getObject(String columnLabel) {
            return rows.get(columnLabel);
        }

    }
}
