package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import static org.assertj.core.api.Assertions.assertThat;
import static persistence.sql.dml.BooleanExpression.eq;

class SelectQueryBuilderTest {
    private final Columns columns = Columns.createColumns(Person.class);
    private final TableData table = TableData.from(Person.class);
    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(table, columns);

    @Test
    @DisplayName("요구사항2: findAll 쿼리 생성")
    void testFindAll() {
        String expected = "select id, nick_name, old, email from users";
        WhereBuilder booleanBuilder = new WhereBuilder();
        String selectQuery = selectQueryBuilder.build(booleanBuilder);

        assertThat(selectQuery).isEqualTo(expected);
    }

    @Test
    @DisplayName("요구사항3: findById 쿼리 생성")
    void testFindById() {
        int id = 1;
        String expected = String.format("select id, nick_name, old, email from users where id = %s", id);
        WhereBuilder booleanBuilder = new WhereBuilder();
        booleanBuilder.and(eq("id", id));
        String selectQuery = selectQueryBuilder.build(booleanBuilder);

        assertThat(selectQuery).isEqualTo(expected);
    }
}
