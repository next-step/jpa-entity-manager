package database.sql.dml;

import database.mapping.EntityMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class InsertQueryBuilderTest {
    static List<Arguments> testCases() {
        return List.of(
                arguments(Map.of("nick_name", "abc"), "INSERT INTO users (nick_name) VALUES ('abc')"),
                arguments(Map.of("nick_name", "abc", "old", 14, "email", "a@b.com"),
                          "INSERT INTO users (nick_name, old, email) VALUES ('abc', 14, 'a@b.com')"),
                arguments(Map.of("nick_name", "abc", "old", 14),
                          "INSERT INTO users (nick_name, old) VALUES ('abc', 14)"),
                arguments(new HashMap<String, Object>() {
                    {
                        put("nick_name", null);
                        put("old", 14);
                    }
                }, "INSERT INTO users (nick_name, old) VALUES (NULL, 14)")
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void buildInsertQuery(Map<String, Object> valueMap, String expected) {
        String actual = new InsertQueryBuilder(EntityMetadata.fromClass(Person4.class))
                .values(valueMap)
                .toQueryString();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void insertQueryWithId() {
        Map<String, Object> valueMap = Map.of("nick_name", "abc", "old", 14, "email", "a@b.com");
        String actual = new InsertQueryBuilder(EntityMetadata.fromClass(Person4.class))
                .id(10L)
                .values(valueMap)
                .toQueryString();
        assertThat(actual).isEqualTo("INSERT INTO users (id, nick_name, old, email) VALUES (10, 'abc', 14, 'a@b.com')");
    }

    @Test
    void insertIntoEntityWithNoId() {
        String actual = new InsertQueryBuilder(EntityMetadata.fromClass(NoAutoIncrementUser.class))
                .id(10L)
                .values(Map.of("nick_name", "abc", "old", 14, "email", "a@b.com"))
                .toQueryString();
        assertThat(actual)
                .isEqualTo("INSERT INTO users_no_auto_increment (id, nick_name, old, email) VALUES (10, 'abc', 14, 'a@b.com')");
    }
}
