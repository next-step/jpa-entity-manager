package persistence.entity;

import persistence.entity.persistencecontext.EntityKey;
import persistence.entity.persistencecontext.EntitySnapshot;
import persistence.sql.meta.Table;

public class EntityEntry {
    private EntityState state;

    private EntityEntry(EntityState state) {
        this.state = state;
    }

    public static EntityEntry saving() {
        return new EntityEntry(EntityState.SAVING);
    }

    public static EntityEntry loading() {
        return new EntityEntry(EntityState.LOADING);
    }

    public void managed() {
        this.state = EntityState.MANAGED;
    }

    public void deleted() {
        this.state = EntityState.DELETED;
    }

    public void gone() {
        this.state = EntityState.GONE;
    }

    public enum EntityState {
        MANAGED,
        READ_ONLY,
        DELETED,
        GONE,
        LOADING,
        SAVING
    }
}


