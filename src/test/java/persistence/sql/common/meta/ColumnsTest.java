package persistence.sql.common.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.SelectPerson;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsTest {
    @Test
    @DisplayName("칼럼명을 콤마로 이어 반환합니다.")
    void getColumnsWithComma() {
        //given
        final String expected = "select_person_id, nick_name, old, email";

        final Columns columns = Columns.of((SelectPerson.class).getDeclaredFields());

        //when
        String result = columns.getColumnsWithComma();

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("@Id의 칼럼명을 가져옵니다.")
    void getIdName() {
        //given
        final String expected = "select_person_id";

        final Columns columns = Columns.of((SelectPerson.class).getDeclaredFields());

        //when
        String result = columns.getIdName();

        //then
        assertThat(result).isEqualTo(expected);
    }
}