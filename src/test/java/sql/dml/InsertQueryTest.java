package sql.dml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import persistence.sql.ddl.Person;
import persistence.sql.dml.InsertQuery;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredObjectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InsertQueryTest {

    @Test
    void 데이터_삽입() {
        final String name = "이름";
        final Integer age = 11;
        final String email = "email@test.com";
        Person person = new Person(name, age, email, null);

        InsertQuery insertQuery = InsertQuery.getInstance();
        String sql = insertQuery.makeQuery(person);

        assertThat(sql).isEqualTo("INSERT INTO users (nick_name, old, email) VALUES ('이름', 11, 'email@test.com')");
    }

    @ParameterizedTest
    @NullSource
    void 매개변수_값이_Null_일때(Object object) {
        assertThatThrownBy(() -> InsertQuery.getInstance().makeQuery(object))
                .isInstanceOf(RequiredObjectException.class)
                .hasMessage(ExceptionMessage.REQUIRED_OBJECT.getMessage());
    }

}