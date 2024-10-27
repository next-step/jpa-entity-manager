package persistence.sql.dml.query.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.query.InsertQuery;
import sample.domain.Person;

public class InsertQueryBuilderTest {

    @Test
    @DisplayName("[성공] Person Entity 테이블에 대한 insert query 검증")
    void insertQuery() {
        Person person = new Person("person name", 20, "person@email.com");
        InsertQuery query = new InsertQuery(person);
        InsertQueryBuilder queryBuilder = InsertQueryBuilder.builder()
                .insert(
                        query.tableName(),
                        query.columns()
                )
                .values(query.columns());
        String expectedQuery = """
                insert into users (nick_name, old, email) values ('person name', 20, 'person@email.com')""";
        assertEquals(queryBuilder.build(), expectedQuery);
    }

}
