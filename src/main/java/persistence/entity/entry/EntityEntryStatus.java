package persistence.entity.entry;

import java.util.ArrayList;
import java.util.List;

public enum EntityEntryStatus {
    GONE,
    READ_ONLY,
    DELETED(List.of(GONE)),
    MANAGED(List.of(DELETED, READ_ONLY)),
    LOADING(List.of(MANAGED, READ_ONLY)),
    SAVING(List.of(MANAGED));

    private final List<EntityEntryStatus> validTransitions;

    EntityEntryStatus(List<EntityEntryStatus> validTransitions) {
        this.validTransitions = validTransitions;
    }

    EntityEntryStatus() {
        this.validTransitions = new ArrayList<>();
    }

    public boolean isTransitiveTo(EntityEntryStatus newStatus) {
        return validTransitions.contains(newStatus);
    }
}
