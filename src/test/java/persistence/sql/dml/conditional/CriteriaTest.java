package persistence.sql.dml.conditional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.entity.model.Operators;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CriteriaTest {

    @DisplayName("빈 조건문을 반환한다.")
    @Test
    void emptyCriteria() {
        Criteria criteria = Criteria.emptyInstance();

        assertThat(criteria.toSql()).isEqualTo("");
    }

    @DisplayName("지정된 조건문을 반환한다.")
    @Test
    void existsCriteria() {
        Criterion criterion = new Criterion("name", "1", Operators.EQUALS);
        Criteria criteria = new Criteria(Collections.singletonList(criterion));

        assertThat(criteria.toSql()).isEqualTo("name='1'");
    }

}
