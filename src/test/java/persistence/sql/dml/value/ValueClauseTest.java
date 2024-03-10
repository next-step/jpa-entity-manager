package persistence.sql.dml.value;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.notcolumn.Person;

import java.lang.reflect.Field;
import java.util.Arrays;
class ValueClauseTest {

    @Test
    @DisplayName("field가 String일 경우 작은따옴표를 값의 양쪽에 붙인다.")
    void stringTest() throws NoSuchFieldException {

        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        var field = getFieldByName(person, "name");
        var value = new ValueClause(field, person).value();
        Assertions.assertThat(value).isEqualTo("'김철수'");
    }

    @Test
    @DisplayName("field가 숫자일 경우 작은따옴표를 값의 양쪽에 붙이지 않는다.")
    void numberTest() throws NoSuchFieldException {
        var person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        var field = getFieldByName(person, "age");
        var value = new ValueClause(field, person).value();
        Assertions.assertThat(value).isEqualTo("21");
    }

    private Field getFieldByName(Person person, String fieldName) {
        return Arrays.stream(person.getClass().getDeclaredFields()).filter(x -> x.getName().equals(fieldName)).findFirst().get();
    }
}