package persistence.entity.context;

import java.util.HashMap;
import java.util.Map;

import static persistence.entity.context.Status.*;

public class EntityEntries {
    private final Map<EntityKey, EntityEntry> entityEntriesMap;

    public EntityEntries() {
        this.entityEntriesMap = new HashMap<>();
    }

    public void manage(EntityKey entityKey) {
        setStatus(entityKey, MANAGED);
    }

    public void setAsDeleted(EntityKey entityKey) {
        setStatus(entityKey, DELETED);
    }

    public void gone(EntityKey entityKey) {
        setStatus(entityKey, GONE);
    }

    public boolean canGet(EntityKey entityKey) {
        switch (getStatus(entityKey)) {
            case NONE:
                return false;
            case LOADING:
            case SAVING:
                throw new UnsupportedOperationException();
            case DELETED:
            case GONE:
                throw new ObjectNotFoundException();
            case MANAGED:
            case READ_ONLY:
                return true;
        }
        return false;
    }

    public boolean canAdd(EntityKey entityKey) {
        switch (getStatus(entityKey)) {
            case NONE:
            case LOADING:
            case MANAGED:
                return true;
            case SAVING:
                // 아무것도 안함
                return false;
            case READ_ONLY:
                throw new UnsupportedOperationException();
            case DELETED:
            case GONE:
                throw new ObjectNotFoundException();
        }
        return false;
    }

    public boolean isRemoved(EntityKey entityKey) {
        switch (getStatus(entityKey)) {
            case NONE:
            case LOADING:
            case MANAGED:
            case SAVING:
            case READ_ONLY:
                return false;
            case DELETED:
            case GONE:
                return true;
        }
        return false;
    }

    public boolean canRemove(EntityKey entityKey) {
        switch (getStatus(entityKey)) {
            case MANAGED:
                return true;
            case NONE:
            case LOADING:
            case SAVING:
            case READ_ONLY:
            case DELETED:
            case GONE:
                return false;
        }
        return false;
    }

    private Status getStatus(EntityKey entityKey) {
        if (!entityEntriesMap.containsKey(entityKey)) {
            return Status.NONE;
        }
        return entityEntriesMap.get(entityKey).getStatus();
    }

    private void setStatus(EntityKey entityKey, Status status) {
        if (!entityEntriesMap.containsKey(entityKey)) {
            entityEntriesMap.put(entityKey, new EntityEntry(status));
        } else {
            entityEntriesMap.get(entityKey).setStatus(status);
        }
    }
}
