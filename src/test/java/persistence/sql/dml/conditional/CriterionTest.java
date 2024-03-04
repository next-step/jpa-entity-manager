package persistence.sql.dml.conditional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.entity.model.Operators;

import static org.assertj.core.api.Assertions.assertThat;

class CriterionTest {

    @DisplayName("조건문 저장한 값을 반환한다.")
    @Test
    void customCriterion() {
        Criterion criterion = Criterion.of("id", "nextstep");

        assertThat(criterion.toSql()).isEqualTo("id='nextstep'");
    }

    @DisplayName("동일한 값을 체크하는 조건문을 반환한다.")
    @Test
    void equlasCriterion() {
        Criterion criterion = Criterion.of("id", "123");

        assertThat(criterion.toSql()).isEqualTo("id='123'");
    }

}
