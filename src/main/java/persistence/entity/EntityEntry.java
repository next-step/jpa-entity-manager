package persistence.entity;

import persistence.entity.type.EntityStatus;

public interface EntityEntry {
    EntityStatus getStatus();

    boolean isReadOnly();
}
