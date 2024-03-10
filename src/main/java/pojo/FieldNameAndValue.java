package pojo;

import java.util.Objects;

public class FieldNameAndValue {

    private final FieldName fieldName;
    private final FieldValue fieldValue;

    public FieldNameAndValue(FieldName fieldName, FieldValue fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public FieldName getFieldName() {
        return fieldName;
    }

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    public String joinNameAndValueWithDelimiter(String delimiter) {
        return String.join(delimiter, getFieldName().getName(), String.valueOf(getFieldValue().getValue()));
    }

    public boolean isNotBlankOrEmpty() {
        return Objects.nonNull(fieldName) && Objects.nonNull(fieldValue);
    }
}
