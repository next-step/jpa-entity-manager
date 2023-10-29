package persistence.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnapShots {
    private final Map<Class<?>, Map<String, Object>> snapShots = new HashMap<>();

    public Object getSnapShotOrNull(Class<?> clazz, String instanceId) {
        return Optional.ofNullable(snapShots.get(clazz))
                .map(snapShot -> snapShot.get(instanceId))
                .orElse(null);
    }

    public void remove(Class<?> clazz, String instanceId) {
        Optional.ofNullable(snapShots.get(clazz))
                .ifPresent(snapShot -> {
                    snapShot.remove(instanceId);
                    if (snapShot.isEmpty()) {
                        snapShots.remove(clazz);
                    }
                });
    }

    public void putSnapShot(Object instance, String instanceId) {
        if (instance == null) {
            return;
        }
        snapShots.computeIfAbsent(instance.getClass(), k -> new HashMap<>())
                .put(instanceId, instance);
    }
}
