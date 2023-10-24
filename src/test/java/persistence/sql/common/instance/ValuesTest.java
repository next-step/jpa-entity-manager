package persistence.sql.common.instance;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesTest {
    @Test
    @DisplayName("업데이트 쿼리를 위하여 ID는 제외한 필드명과 값 관련 문자열 반환")
    void expectResult() {
        //given
        final String expectResult = "nick_name = 'Zz', old = 3, email = 'zz'";
        final Person person = new Person(3L, "Zz", 3, "zz", 1);

        //when
        String result = Values.getFieldNameAndValue(person);

        //then
        assertThat(result).isEqualTo(expectResult);
    }
}
