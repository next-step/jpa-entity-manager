package persistence.sql.domain;

public interface ColumnOperation {

    String getJdbcColumnName();

    String getColumnValue();

    boolean hasColumnValue();

    Class<?> getColumnObjectType();

    Integer getColumnSize();

    boolean isPrimaryColumn();

    boolean isNullable();

    String getJavaFieldName();

    default Object getColumnValueByJavaType(){
        String value = getColumnValue();
        Class<?> type = getColumnObjectType();
        if (type.equals(Long.class)){
            return Long.valueOf(value);
        }
        return value;
    }
}
