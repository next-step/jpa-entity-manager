package persistence.entity;

import persistence.util.ReflectionUtil;

import java.util.Map;
import java.util.Objects;

public class EntitySnapshot {
    private Object originalEntity;
    private Map<String, Object> keyValues;
    private boolean isDirty;

    public EntitySnapshot(Object entity) {
        this.originalEntity = entity;
        this.keyValues = ReflectionUtil.getAllFieldNameAndValue(entity);
        this.isDirty = false;
    }

    public Object getOriginalEntity() {
        return originalEntity;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public void update(Object entity) {
        if (shouldBeDirty(entity)) {
            this.originalEntity = entity;
            this.keyValues = ReflectionUtil.getAllFieldNameAndValue(entity);
            isDirty = true;
        }
    }

    private boolean shouldBeDirty(Object currentEntity) {
        return keyValues
                .entrySet()
                .stream()
                .anyMatch(entry -> {
                    return !Objects.equals(
                            entry.getValue(),
                            ReflectionUtil.getFieldValue(currentEntity, entry.getKey())
                    );
                });
    }
}
