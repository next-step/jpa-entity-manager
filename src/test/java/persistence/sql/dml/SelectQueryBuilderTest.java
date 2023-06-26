package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private final SelectQueryBuilder builder = new SelectQueryBuilder(Person.class);

    @DisplayName("모든 엔티티를 조회하는 SELECT 쿼리를 생성한다.")
    @Test
    void buildFindAllQuery() {
        String query = builder.buildFindAllQuery();
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @DisplayName("식별자로 하나의 엔티티를 조회하는 SELECT 쿼리를 생성한다.")
    @Test
    void buildFindByIdQuery() {
        String query = builder.buildFindByIdQuery(1L);
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }
}
