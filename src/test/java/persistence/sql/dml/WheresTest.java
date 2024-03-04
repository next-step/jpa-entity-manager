package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.Value;

import java.sql.Types;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WheresTest {

    @DisplayName("where 문을 생성한다")
    @Test
    public void whereClause() throws Exception {
        // given
        final Value value1 = new Value(String.class, Types.VARCHAR, "이름", "'이름'");
        final Column column1 = new Column("name", Types.VARCHAR, value1);
        final Value value2 = new Value(Integer.class, Types.INTEGER, 1, "1");
        final Column column2 = new Column("age", Types.INTEGER, value2);
        final Value value3 = new Value(String.class, Types.VARCHAR, "email@domain.com", "'email@domain.com'");
        final Column column3 = new Column("email", Types.VARCHAR, value3);
        final List<Where> whereList = List.of(
                new Where(column1, value1, LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)),
                new Where(column2, value2, LogicalOperator.AND, new ComparisonOperator(ComparisonOperator.Comparisons.LE)),
                new Where(column3, value3, LogicalOperator.OR, new ComparisonOperator(ComparisonOperator.Comparisons.EQ))
        );

        final Wheres wheres = new Wheres(whereList);

        final String whereClause = "name = '이름'\n" +
                "    and age <= 1\n" +
                "    or email = 'email@domain.com'";

        // when
        final String result = wheres.wheresClause();

        // then
        assertThat(result).isEqualTo(whereClause);
    }

}
