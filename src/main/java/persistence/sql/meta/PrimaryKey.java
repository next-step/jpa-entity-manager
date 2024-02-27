package persistence.sql.meta;

import jakarta.persistence.GenerationType;

public interface PrimaryKey {
    String name();
    String value(final Object object);
    Class<?> type();
    GenerationType strategy();

}
