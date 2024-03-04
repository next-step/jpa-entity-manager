package domain.vo;

import java.util.Objects;

public class ColumnNameAndValue {

    private final ColumnName columnName;
    private final ColumnValue columnValue;

    public ColumnNameAndValue(ColumnName columnName, ColumnValue columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public ColumnName getColumnName() {
        return columnName;
    }

    public ColumnValue getColumnValue() {
        return columnValue;
    }

    public String joinNameAndValueWithDelimiter(String delimiter) {
        return String.join(delimiter, getColumnName().getName(), String.valueOf(getColumnValue().getValue()));
    }

    public boolean isNotBlankOrEmpty() {
        return Objects.nonNull(columnName) && Objects.nonNull(columnValue);
    }
}
