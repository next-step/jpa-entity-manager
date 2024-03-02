package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.MysqlDialect;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    @Test
    @DisplayName("update 쿼리를 만들 수 있다.")
    void updateById() {
        Dialect dialect = new MysqlDialect();
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(dialect);
        Person person = new Person(1L, "name", 10, "jon@test.com", 1);
        String query = updateQueryBuilder.build(person).toStatementWithId(1L);
        assertThat(query).isEqualTo("update users set nick_name = 'name', old = 10, email = 'jon@test.com' where id = 1");
    }
}
