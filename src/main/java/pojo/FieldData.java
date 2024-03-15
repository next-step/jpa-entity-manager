package pojo;

public interface FieldData {

    boolean isIdField();

    boolean isColumnField();

    boolean isNotTransientField();

    boolean isNullableField();

    String getFieldNameData();

    Object getFieldValueData();
}
