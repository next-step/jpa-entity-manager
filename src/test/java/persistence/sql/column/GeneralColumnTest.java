package persistence.sql.column;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.Person;
import persistence.sql.dialect.MysqlDialect;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GeneralColumnTest {

    @DisplayName("column의 정의를 반환한다.")
    @ParameterizedTest
    @MethodSource
    void getDefinition(String fieldName, String result) throws NoSuchFieldException {
        //given
        Field field = Person.class.getDeclaredField(fieldName);
        GeneralColumn generalColumn = new GeneralColumn(field, new MysqlDialect());

        //when
        //then
        assertThat(generalColumn.getDefinition()).isEqualTo(result);
    }

    private static Stream<Arguments> getDefinition() {
        return Stream.of(
                Arguments.of("name", "nick_name varchar(255)"),
                Arguments.of("age", "old integer"),
                Arguments.of("email", "email varchar(255) not null")
        );
    }

    @DisplayName("column의 바뀐 이름을 반환한다.")
    @ParameterizedTest
    @MethodSource
    void getName(String fieldName, String result) throws NoSuchFieldException {
        //given
        Field field = Person.class.getDeclaredField(fieldName);

        //when
        GeneralColumn generalColumn = new GeneralColumn(field, new MysqlDialect());

        //then
        assertThat(generalColumn.getName()).isEqualTo(result);
    }

    private static Stream<Arguments> getName() {
        return Stream.of(
                Arguments.of("name", "nick_name"),
                Arguments.of("age", "old"),
                Arguments.of("email", "email")
        );
    }

    @DisplayName("column의 필드 이름을 반환한다.")
    @ParameterizedTest
    @MethodSource
    void getFieldName(String fieldName, String result) throws NoSuchFieldException {
        //given
        Field field = Person.class.getDeclaredField(fieldName);

        //when
        GeneralColumn generalColumn = new GeneralColumn(field, new MysqlDialect());

        //then
        assertThat(generalColumn.getFieldName()).isEqualTo(result);
    }

    private static Stream<Arguments> getFieldName() {
        return Stream.of(
                Arguments.of("name", "name"),
                Arguments.of("age", "age"),
                Arguments.of("email", "email")
        );
    }
}
