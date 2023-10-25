package persistence.sql.common.instance;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.common.meta.Columns;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesTest {
    @Test
    @DisplayName("값을 콤마로 이어 출력")
    void getValuesWithComma() {
        //given
        final String expectResult = "3, 'Zz', 3, 'zz'";
        final Person person = new Person(3L, "Zz", 3, "zz", 1);

        final Values values = Values.of(person);

        //when
        String result = values.getValuesWithComma();

        //then
        assertThat(result).isEqualTo(expectResult);
    }

    @Test
    @DisplayName("업데이트 쿼리를 위하여 ID는 제외한 필드명과 값 관련 문자열 반환")
    void expectResult() {
        //given
        final String expectResult = "nick_name = 'Zz', old = 3, email = 'zz'";
        final Person person = new Person(3L, "Zz", 3, "zz", 1);

        final Columns columns = Columns.of(person.getClass().getDeclaredFields());
        final Values values = Values.of(person);

        //when
        String result = values.getFieldNameAndValue(columns);

        //then
        assertThat(result).isEqualTo(expectResult);
    }
}
