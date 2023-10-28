package persistence.entity.attribute.id;

import jakarta.persistence.GenerationType;
import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;

public interface IdAttribute {
    String prepareDDL(SqlConverter sqlConverter);

    Field getField();

    String getColumnName();

    String getFieldName();

    GenerationType getGenerationType();
}
