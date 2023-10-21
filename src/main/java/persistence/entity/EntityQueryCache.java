package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EntityQueryCache<KEY> {
    private final Map<EntityQueryType, Map<KEY, String>> queryCache;

    public EntityQueryCache() {
        this.queryCache = new HashMap<>();
        initQueryCache();
    }

    private void initQueryCache() {
        for (final EntityQueryType queryType : EntityQueryType.values()) {
            queryCache.put(queryType, new HashMap<>());
        }
    }

    public String computeIfAbsent(final EntityQueryType queryType, final KEY key, final Function<KEY, String> mappingFunction) {
        final Map<KEY, String> cache = queryCache.get(queryType);
        return cache.computeIfAbsent(key, mappingFunction);
    }
}
