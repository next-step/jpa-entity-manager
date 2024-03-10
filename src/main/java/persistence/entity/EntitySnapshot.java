package persistence.entity;

import dialect.Dialect;
import pojo.FieldInfo;
import pojo.FieldInfos;
import pojo.FieldName;
import pojo.FieldValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntitySnapshot {

    private final Map<String, Object> map;

    public EntitySnapshot(Dialect dialect, Object entity) {
        List<FieldInfo> fieldInfos = new FieldInfos(entity.getClass().getDeclaredFields()).getIdAndColumnFieldsData();
        this.map = fieldInfos.stream()
                .map(FieldInfo::getField)
                .collect(Collectors.toMap(
                        field -> new FieldName(field).getName(),
                        field -> new FieldValue(dialect, field, entity).getValue()
                ));
    }

    public Map<String, Object> getMap() {
        return map;
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
