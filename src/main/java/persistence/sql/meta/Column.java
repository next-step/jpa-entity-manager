package persistence.sql.meta;

import jakarta.persistence.GenerationType;

public interface Column {
    String getFieldName();

    Object value();

    Class<?> type();

    GenerationType generateType();

    boolean isNullable();
}
