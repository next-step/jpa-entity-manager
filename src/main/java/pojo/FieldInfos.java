package pojo;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FieldInfos {

    private final List<Field> fieldInfoList;

    public FieldInfos(Field[] fields) {
        if (Objects.isNull(fields)) {
            throw new IllegalArgumentException("fields 가 null 이어서는 안됩니다.");
        }
        this.fieldInfoList = Arrays.stream(fields).collect(Collectors.toList());
    }

    public List<Field> getFieldDataList() {
        return fieldInfoList;
    }

    public Field getIdField() {
        return fieldInfoList.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Id 필드가 존재하지 않습니다."));
    }

    public List<Field> getColumnFields() {
        return fieldInfoList.stream()
                .filter(field -> !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(Id.class))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Field> getIdAndColumnFields() {
        return fieldInfoList.stream()
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
