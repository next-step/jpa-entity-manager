package persistence.sql.meta;

import jakarta.persistence.GenerationType;

public interface Column {
    String getFieldName();

    String value(Object object);

    Class<?> type();

    GenerationType generateType();

    boolean isNullable();
}
