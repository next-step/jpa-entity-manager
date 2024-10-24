package sql.dml;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredObjectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteQueryTest {

    @Test
    void DELETE_쿼리_생성() {
        Person person = new Person(1L, "name", 1, "email@email.com");
        DeleteQuery deleteQueryBuilder = DeleteQuery.getInstance();
        assertThat(deleteQueryBuilder.makeQuery(person)).isEqualTo("DELETE FROM users WHERE id=1");
    }

    @Test
    void 객체_생성시_Null_일_경우() {
        assertThatThrownBy(() -> DeleteQuery.getInstance().makeQuery(null))
                .isInstanceOf(RequiredObjectException.class)
                .hasMessage(ExceptionMessage.REQUIRED_OBJECT.getMessage());
    }

}
