package persistence.sql.column;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.dialect.MysqlDialect;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IdColumn을 제외한")
class ColumnsTest {

    @DisplayName("column들의 정의를 반환한다.")
    @Test
    void getColumnsDefinition() {
        // given
        Columns columns = new Columns(Person.class.getDeclaredFields(), new MysqlDialect());
        // when
        String result = columns.getColumnsDefinition();

        // then
        assertThat(result).isEqualTo("nick_name varchar(255), old integer, email varchar(255) not null");
    }

    @DisplayName("column들의 이름을 반환한다.")
    @Test
    void getColumnNames() {
        // given
        Columns columns = new Columns(Person.class.getDeclaredFields(), new MysqlDialect());
        // when
        String result = columns.getColumnNames();

        // then
        assertThat(result).isEqualTo("nick_name, old, email");
    }
}
