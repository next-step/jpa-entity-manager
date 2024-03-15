package persistence.entity;

import pojo.FieldInfo;
import pojo.FieldInfos;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntitySnapshot {

    private final Map<String, Object> map;

    public EntitySnapshot(Object entity) {
        List<Field> fields = new FieldInfos(entity.getClass().getDeclaredFields()).getIdAndColumnFields();
        this.map = fields.stream()
                .map(field -> new FieldInfo(field, entity))
                .collect(Collectors.toMap(
                        fieldInfo -> fieldInfo.getFieldName().getName(),
                        fieldInfo -> fieldInfo.getFieldValue().getValue()
                ));
    }

    public Map<String, Object> getMap() {
        return new HashMap<>(map);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntitySnapshot that = (EntitySnapshot) object;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}
