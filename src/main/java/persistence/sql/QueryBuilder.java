package persistence.sql;

import jakarta.persistence.Transient;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.ddl.dialect.H2Dialect;

import java.lang.reflect.Field;

public interface QueryBuilder {

    String COMMA = ", ";
    String SPACE = " ";
    String EMPTY_STRING = "";

    Dialect DIALECT = new H2Dialect();

    String build();

    default boolean isNotTransientAnnotationPresent(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

}
