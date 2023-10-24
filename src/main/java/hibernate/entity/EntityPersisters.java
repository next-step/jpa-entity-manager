package hibernate.entity;

import java.util.Map;

public class EntityPersisters {

    private final Map<Class<?>, EntityPersister<?>> values;

    public EntityPersisters(Map<Class<?>, EntityPersister<?>> values) {
        this.values = values;
    }

    public EntityPersister<?> findEntityPersister(final Object object) {
        return values.entrySet()
                .stream()
                .filter(value -> value.getKey() == object.getClass())
                .findAny()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Entity 클래스입니다."));
    }
}
