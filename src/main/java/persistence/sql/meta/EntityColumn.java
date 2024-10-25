package persistence.sql.meta;

import java.lang.reflect.Field;
import java.util.Objects;

public class EntityColumn {
    private final Class<?> type;
    private final ColumnName columnName;
    private final ColumnLength columnLength;
    private final ColumnIdOption columnIdOption;
    private final ColumnOption columnOption;
    private final ColumnValue columnValue;

    public EntityColumn(Field field) {
        this.type = field.getType();
        this.columnName = new ColumnName(field);
        this.columnLength = new ColumnLength(field);
        this.columnIdOption = new ColumnIdOption(field);
        this.columnOption = new ColumnOption(field);
        this.columnValue = new ColumnValue(field);
    }

    public EntityColumn(Field field, Object entity) {
        this.type = field.getType();
        this.columnName = new ColumnName(field);
        this.columnLength = new ColumnLength(field);
        this.columnIdOption = new ColumnIdOption(field);
        this.columnOption = new ColumnOption(field);
        this.columnValue = new ColumnValue(field, entity);
    }

    public Class<?> getType() {
        return type;
    }

    public String getColumnName() {
        return columnName.value();
    }

    public int getColumnLength() {
        return columnLength.value();
    }

    public boolean isId() {
        return columnIdOption.isId();
    }

    public boolean isGenerationValue() {
        return columnIdOption.isGenerationValue();
    }

    public boolean isNotNull() {
        return columnOption.isNotNull();
    }

    public Object getValue() {
        return columnValue.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityColumn that = (EntityColumn) o;
        return Objects.equals(type, that.type) && Objects.equals(columnName, that.columnName)
                && Objects.equals(columnLength, that.columnLength) && Objects.equals(columnIdOption, that.columnIdOption)
                && Objects.equals(columnOption, that.columnOption) && Objects.equals(columnValue, that.columnValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, columnName, columnLength, columnIdOption, columnOption, columnValue);
    }

    public String getValueWithQuotes() {
        return columnValue.valueWithQuotes();
    }

    public boolean isIdGenerationFromDatabase() {
        return columnIdOption.isIdGenerationFromDatabase();
    }
}
