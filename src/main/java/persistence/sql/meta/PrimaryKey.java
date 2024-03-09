package persistence.sql.meta;

import jakarta.persistence.GenerationType;

public interface PrimaryKey {
    String name();
    Object value();
    Class<?> type();
    GenerationType strategy();

}
