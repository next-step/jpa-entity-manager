package persistence.entity.attribute.id;

import jakarta.persistence.GenerationType;
import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;

public abstract class IdAttribute {
    abstract public String prepareDDL(SqlConverter sqlConverter);

    abstract public Field getField();

    abstract public String getColumnName();

    abstract public String getFieldName();

    abstract public GenerationType getGenerationType();
}
