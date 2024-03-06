package database.mapping.column;

import database.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class PrimaryKeyEntityColumn extends AbstractEntityColumn {
    private final boolean autoIncrement;
    private final boolean isRequiredId;

    // field 생성 후 주입이 어려워서 테스트가 쉽지 않다
    public PrimaryKeyEntityColumn(Field field,
                                  String columnName,
                                  Class<?> type,
                                  Integer columnLength,
                                  boolean autoIncrement,
                                  boolean isRequiredId) {
        super(field, columnName, type, columnLength);

        this.autoIncrement = autoIncrement;
        this.isRequiredId = isRequiredId;
    }

    @Override
    public String toColumnDefinition(Dialect dialect) {
        StringJoiner definitionJoiner = new StringJoiner(" ");
        definitionJoiner.add(columnName);
        definitionJoiner.add(dialect.convertToSqlTypeDefinition(type, columnLength));
        if (autoIncrement) {
            definitionJoiner.add(dialect.autoIncrementDefinition());
        }
        definitionJoiner.add(dialect.primaryKeyDefinition());
        return definitionJoiner.toString();
    }

    @Override
    public boolean isPrimaryKeyField() {
        return true;
    }

    public boolean isRequiredId() {
        return isRequiredId;
    }
}
