package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.Metadata;
import persistence.sql.domain.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteQueryBuilderTest {

    @Test
    @DisplayName("Person 객체로 Delete Query 만들기")
    void deleteQuery() {
        Metadata metadata = new Metadata(Person.class);
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(metadata.getEntityTable(), metadata.getEntityColumns());
        String deleteQuery = deleteQueryBuilder.delete(1L);

        assertEquals(deleteQuery, "delete FROM users where id = 1");
    }


}
