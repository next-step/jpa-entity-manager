package persistence.sql.dml.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DummyPerson;

import static org.assertj.core.api.Assertions.assertThat;

class DMLColumnTest {

    private DMLColumn column;

    @BeforeEach
    void setUp() {
        column = new DMLColumn(DummyPerson.of());
    }

    @Test
    @DisplayName("Field 객체가 가지는 Entity 의 필드명들을 조회한다.")
    void fieldsTest() {
        final var expected = "id, nick_name, old, email";

        final var actual = column.getAllColumnClause();

        assertThat(actual).isEqualTo(expected);
    }

    // TODO: 3/10/24 getAllFields(), getIdColumnName() 테스트

}
