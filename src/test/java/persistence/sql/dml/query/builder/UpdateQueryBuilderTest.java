package persistence.sql.dml.query.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.query.UpdateQuery;
import persistence.sql.dml.query.WhereCondition;
import sample.domain.Person;

class UpdateQueryBuilderTest {

    @Test
    @DisplayName("[성공] Person Entity 테이블의 특정 id에 대한 update query")
    void updateQuery() {
        Person person = new Person("person name", 20, "person@email.com");
        UpdateQuery query = new UpdateQuery(person);
        UpdateQueryBuilder queryBuilder = UpdateQueryBuilder.builder()
                .update(query.tableName())
                .set(query.columns())
                .where(List.of(new WhereCondition("id", "=", 1L)));
        String expectedQuery = """
                update users set nick_name = 'person name', old = 20, email = 'person@email.com' where id = 1""";
        assertEquals(queryBuilder.build(), expectedQuery);
    }

}
