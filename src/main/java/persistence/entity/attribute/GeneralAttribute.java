package persistence.entity.attribute;

import persistence.sql.ddl.converter.SqlConverter;

public interface GeneralAttribute {
    String prepareDDL(SqlConverter sqlConverter);

    String getColumnName();

    String getFieldName();
}
