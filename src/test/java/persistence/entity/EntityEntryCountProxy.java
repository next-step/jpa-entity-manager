package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntryCountProxy implements EntityEntry {
    final private EntityEntry target;
    Map<Status, Integer> invokedCount = new HashMap<>();

    public EntityEntryCountProxy(EntityEntry target) {
        this.target = target;
        invokedCount.put(Status.SAVING, 0);
    }

    @Override
    public Status getStatus() {
        return target.getStatus();
    }


    @Override
    public void setSaving() {
        target.setSaving();
        invokedCount.put(Status.SAVING, invokedCount.get(Status.SAVING) + 1);
    }

    @Override
    public void setManaged() {
        target.setManaged();
    }

    @Override
    public void setLoading() {
        target.setLoading();
    }

    @Override
    public void setDeleted() {
        target.setDeleted();
    }

    @Override
    public void setGone() {
        target.setGone();
    }

    @Override
    public void setReadOnly() {
        target.setReadOnly();
    }

    @Override
    public boolean isReadOnly() {
        return target.isReadOnly();
    }
}
