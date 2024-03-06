package database.sql.dml;

import database.mapping.ColumnValueMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(Person4.class);

    enum TestCases {
        WITH_NULL_FIELDS(123L, newPerson4("닉네임", null, null), "UPDATE users SET nick_name = '닉네임', old = NULL, email = NULL WHERE id = 123"),
        WITH_ONE_NULL_FIELD(1314L, newPerson4("abc", 77, null), "UPDATE users SET nick_name = 'abc', old = 77, email = NULL WHERE id = 1314"),
        COMMON_CASE(12345L, newPerson4("abc", 77, "a@example.com"), "UPDATE users SET nick_name = 'abc', old = 77, email = 'a@example.com' WHERE id = 12345");

        final long id;
        final Person4 entity;
        final String expectedQuery;

        TestCases(long id, Person4 entity, String expectedQuery) {
            this.id = id;
            this.entity = entity;
            this.expectedQuery = expectedQuery;
        }
    }

    @ParameterizedTest
    @EnumSource(TestCases.class)
    void buildUpdateQuery(TestCases testCase) {
        Map<String, Object> map = ColumnValueMap.fromEntity(testCase.entity).getMap();
        String actual = updateQueryBuilder.buildQuery(testCase.id, map);
        assertThat(actual).isEqualTo(testCase.expectedQuery);
    }

    private static Person4 newPerson4(String name, Integer age, String email) {
        Person4 person4 = new Person4();
        person4.setName(name);
        person4.setAge(age);
        person4.setEmail(email);
        return person4;
    }
}
