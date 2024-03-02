package database.mapping.column;

import database.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class GeneralEntityColumn extends AbstractEntityColumn {
    private final boolean nullable;

    public GeneralEntityColumn(Field field,
                               String columnName,
                               Class<?> type,
                               Integer columnLength,
                               boolean nullable) {
        super(field, columnName, type, columnLength);

        this.nullable = nullable;
    }

    @Override
    public String toColumnDefinition(Dialect dialect) {
        return new StringJoiner(" ")
                .add(columnName)
                .add(dialect.convertToSqlTypeDefinition(type, columnLength))
                .add(dialect.nullableDefinition(nullable))
                .toString();
    }

    @Override
    public boolean isPrimaryKeyField() {
        return false;
    }

}
