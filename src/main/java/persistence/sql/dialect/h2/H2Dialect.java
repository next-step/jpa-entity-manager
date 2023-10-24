package persistence.sql.dialect.h2;

import jakarta.persistence.Column;
import persistence.sql.Dialect;
import persistence.sql.entity.EntityColumn;

import java.lang.reflect.Field;

/**
 * H2 SQL
 */
public class H2Dialect implements Dialect {

    private static final int VARCHAR_DEFAULT_LENGTH = 255;

    @Override
    public String getDbType(EntityColumn entityColumn) {
        String dbType = H2ColumnType.getDbType(entityColumn.getType());
        if (entityColumn.getType() == String.class) {
            dbType += "(" + getStringLength(entityColumn.getField(), VARCHAR_DEFAULT_LENGTH) + ")";
        }
        return dbType;
    }

    private int getStringLength(Field entityField, int defaultLength) {
        Column columnAnnotation = entityField.getDeclaredAnnotation(Column.class);
        if (columnAnnotation == null) {
            return defaultLength;
        }
        return entityField.getDeclaredAnnotation(Column.class).length();
    }

}
