package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    private final DeleteQueryBuilder builder = new DeleteQueryBuilder(Person.class);

    @DisplayName("모든 엔티티를 삭제하는 DELETE 쿼리를 생성한다.")
    @Test
    void buildDeleteAllQuery() {
        String query = builder.buildDeleteAllQuery();
        assertThat(query).isEqualTo("DELETE FROM users");
    }

    @DisplayName("식별자로 하나의 엔티티를 삭제하는 DELETE 쿼리를 생성한다.")
    @Test
    void buildDeleteByIdQuery() {
        String query = builder.buildDeleteByIdQuery(1L);
        assertThat(query).isEqualTo("DELETE FROM users WHERE id = 1");
    }
}
