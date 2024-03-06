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
        final Value nameValue = new Value(String.class, Types.VARCHAR, "이름", "'이름'");
        final Column nameColumn = new Column("name", Types.VARCHAR, nameValue);
        final Value ageValue = new Value(Integer.class, Types.INTEGER, 1, "1");
        final Column ageColumn = new Column("age", Types.INTEGER, ageValue);
        final Value emailValue = new Value(String.class, Types.VARCHAR, "email@domain.com", "'email@domain.com'");
        final Column emailColumn = new Column("email", Types.VARCHAR, emailValue);
        final List<Where> whereList = List.of(
                new Where(nameColumn, nameValue, LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)),
                new Where(ageColumn, ageValue, LogicalOperator.AND, new ComparisonOperator(ComparisonOperator.Comparisons.LE)),
                new Where(emailColumn, emailValue, LogicalOperator.OR, new ComparisonOperator(ComparisonOperator.Comparisons.EQ))
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
