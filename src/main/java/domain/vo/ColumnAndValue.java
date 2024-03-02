package domain.vo;

import java.util.Objects;

public class ColumnAndValue {

    private final ColumnName columnName;
    private final ColumnValue columnValue;

    public ColumnAndValue(ColumnName columnName, ColumnValue columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public ColumnName getColumnName() {
        return columnName;
    }

    public ColumnValue getColumnValue() {
        return columnValue;
    }

    public boolean isNotBlankOrEmpty() {
        return Objects.nonNull(columnName) && Objects.nonNull(columnValue);
    }
}
