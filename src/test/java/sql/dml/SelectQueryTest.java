package sql.dml;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;
import persistence.sql.dml.SelectQuery;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredClassException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectQueryTest {

    @Test
    void SELECT_쿼리_조회() {
        SelectQuery selectQuery = SelectQuery.getInstance();
        assertThat(selectQuery.findAll(Person.class)).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    void 매개변수_NULL로_예외_발생() {
        assertThatThrownBy(() -> SelectQuery.getInstance().findAll(null))
                .isInstanceOf(RequiredClassException.class)
                .hasMessage(ExceptionMessage.REQUIRED_CLASS.getMessage());
    }

    @Test
    void 아이디로_조회_쿼리() {
        SelectQuery selectQuery = SelectQuery.getInstance();
        assertThat(selectQuery.findById(Person.class, 1L)).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id=1");
    }

}
