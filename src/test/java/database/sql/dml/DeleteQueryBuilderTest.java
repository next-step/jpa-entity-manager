package database.sql.dml;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(Person4.class);

    enum TestCases {
        BY_PRIMARY_KEY(Map.of("nick_name", "foo"),
                       "DELETE FROM users WHERE nick_name = 'foo'"),
        TWO_CONDITIONS1(Map.of("old", 18, "email", "example@email.com"),
                       "DELETE FROM users WHERE old = 18 AND email = 'example@email.com'"),
        TWO_CONDITIONS2(Map.of("nick_name", "foo"),
                        "DELETE FROM users WHERE nick_name = 'foo'"),
        ONE_CONDITIONS1(Map.of("old", 18),
                        "DELETE FROM users WHERE old = 18"),
        ONE_CONDITIONS2(Map.of("nick_name", "foo"),
                        "DELETE FROM users WHERE nick_name = 'foo'");

        final Map<String, Object> conditionMap;
        final String expectedQuery;

        TestCases(Map<String, Object> conditionMap, String expectedQuery) {
            this.conditionMap = conditionMap;
            this.expectedQuery = expectedQuery;
        }
    }

    @ParameterizedTest
    @EnumSource(TestCases.class)
    void assertDeleteQuery(TestCases testCases) {
        Map<String, Object> where = testCases.conditionMap;
        String expectedQuery = testCases.expectedQuery;

        assertThat(deleteQueryBuilder.buildQuery(where)).isEqualTo(expectedQuery);
    }
}
