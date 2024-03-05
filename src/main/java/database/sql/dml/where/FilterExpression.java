package database.sql.dml.where;

public class FilterExpression {
    public static final String EMPTY = "";

    private final String columnName;
    private final OperatorType operator;
    private final Object value;
    private final ValueType valueType;

    private FilterExpression(String columnName, Object value, ValueType valueType, OperatorType operator) {
        this.columnName = columnName;
        this.valueType = valueType;
        this.value = value;
        this.operator = operator;
    }

    public static FilterExpression from(String columnName, Object value) {
        ValueType valueType = ValueType.examineType(value);
        OperatorType operator = OperatorType.examineType(valueType);
        return new FilterExpression(columnName, value, valueType, operator);
    }

    public String toQuery() {
        String operator = this.operator.toQuery();
        String valueString = valueType.toQueryString(value);
        return String.format("%s %s %s", columnName, operator, valueString);
    }
}
