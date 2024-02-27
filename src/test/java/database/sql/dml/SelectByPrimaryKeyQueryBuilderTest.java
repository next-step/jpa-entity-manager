package database.sql.dml;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelectByPrimaryKeyQueryBuilderTest {
    private final SelectByPrimaryKeyQueryBuilder selectByPrimaryKeyQueryBuilder =
            new SelectByPrimaryKeyQueryBuilder(Person4.class);

    @Test
    void buildSelectPrimaryKeyQuery() {
        String actual = selectByPrimaryKeyQueryBuilder.buildQuery(1L);
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }
}
