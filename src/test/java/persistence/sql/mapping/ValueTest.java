package persistence.sql.mapping;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.QueryNumberValueBinder;
import persistence.sql.dml.QueryStringValueBinder;
import persistence.sql.dml.QueryValueBinder;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ValueTest {

    private final QueryValueBinder stringValueBinder = new QueryStringValueBinder();
    private final QueryValueBinder numberValueBinder = new QueryNumberValueBinder();
    
    @DisplayName("value 객체를 생성해 각 값들이 적절하게 들어갔는지 확인한다")
    @Test
    public void valueConstructor() throws Exception {
        // given
        final Class<String> stringClass = String.class;
        final Class<Integer> integerClass = Integer.class;
        final Class<Long> longClass = Long.class;
        final int varcharType = Types.VARCHAR;
        final int integerType = Types.INTEGER;
        final int bigintType = Types.BIGINT;
        final String originalNameValue = "name";
        final int originalIntValue = 1;
        final long originalLongValue = 1L;
        final String originalEmailValue = "email";
        final String emailClause = "'email'";

        // when
        final Value nullValue = new Value(stringClass, varcharType);
        final Value nameValue = new Value(stringClass, varcharType, originalNameValue);
        final Value intValue = new Value(integerClass, integerType, originalIntValue);
        final Value longValue = new Value(longClass, bigintType, originalLongValue);
        final Value emailValue = new Value(stringClass, varcharType, originalEmailValue, emailClause);
        
        // then
        assertAll(
                () -> assertThat(nullValue)
                        .extracting("originalType", "sqlType", "value", "valueClause")
                        .contains(stringClass, varcharType, null, null),
                () -> assertThat(nameValue)
                        .extracting("originalType", "sqlType", "value", "valueClause")
                        .contains(stringClass, varcharType, originalNameValue, stringValueBinder.bind(originalNameValue)),
                () -> assertThat(intValue)
                        .extracting("originalType", "sqlType", "value", "valueClause")
                        .contains(integerClass, integerType, originalIntValue, numberValueBinder.bind(originalIntValue)),
                () -> assertThat(longValue)
                        .extracting("originalType", "sqlType", "value", "valueClause")
                        .contains(longClass, bigintType, originalLongValue, numberValueBinder.bind(originalLongValue)),
                () -> assertThat(emailValue)
                        .extracting("originalType", "sqlType", "value", "valueClause")
                        .contains(stringClass, varcharType, originalEmailValue, emailClause)
        );
    }

}
