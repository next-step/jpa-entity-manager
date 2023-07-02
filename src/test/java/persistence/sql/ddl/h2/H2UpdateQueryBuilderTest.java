package persistence.sql.ddl.h2;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.ColumnMap;
import persistence.sql.ddl.UpdateQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class H2UpdateQueryBuilderTest {

    @Test
    void build_query() {
        UpdateQueryBuilder updateQueryBuilder = new H2UpdateQueryBuilder();
        ColumnMap columnMap = ColumnMap.empty();
        columnMap.add("name", "slow");
        columnMap.add("email", "email@email.com");

        String actual = updateQueryBuilder.createUpdateBuild(1L, "person", columnMap);

        assertThat(actual).isEqualTo("UPDATE person SET name = 'slow', email = 'email@email.com' WHERE id=1");
    }

}
