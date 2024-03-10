package pojo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FieldInfos {

    private final List<FieldInfo> fieldInfoList;

    public FieldInfos(Field[] fields) {
        if (Objects.isNull(fields)) {
            throw new IllegalArgumentException("fields 가 null 이어서는 안됩니다.");
        }
        this.fieldInfoList = Arrays.stream(fields).map(FieldInfo::new).collect(Collectors.toList());
    }

    public List<FieldInfo> getFieldDataList() {
        return fieldInfoList;
    }

    public FieldInfo getIdFieldData() {
        return fieldInfoList.stream()
                .filter(FieldInfo::isIdField)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Id 필드가 존재하지 않습니다."));
    }

    public List<FieldInfo> getColumnFieldsData() {
        return fieldInfoList.stream()
                .filter(fieldData -> fieldData.isNotTransientField() && !fieldData.isIdField())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<FieldInfo> getIdAndColumnFieldsData() {
        return fieldInfoList.stream()
                .filter(FieldInfo::isNotTransientField)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
