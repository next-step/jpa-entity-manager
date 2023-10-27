package hibernate.entity.meta.column;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EntityColumns {

    private final List<EntityColumn> values;

    public EntityColumns(final Field[] fields) {
        this.values = parseToEntityColumns(fields);
    }

    private static List<EntityColumn> parseToEntityColumns(final Field[] fields) {
        return Arrays.stream(fields)
                .filter(EntityColumn::isAvailableCreateEntityColumn)
                .map(EntityColumn::create)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public EntityColumn getEntityId() {
        return values.stream()
                .filter(EntityColumn::isId)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("id field가 없습니다."));
    }

    public Map<EntityColumn, Object> getFieldValues(final Object entity) {
        return values.stream()
                .map(entityColumn -> new AbstractMap.SimpleEntry<>(entityColumn, entityColumn.getFieldValue(entity)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    public List<EntityColumn> getValues() {
        return values;
    }

    public List<String> getFieldNames() {
        return values.stream()
                .map(EntityColumn::getFieldName)
                .collect(Collectors.toList());
    }
}
