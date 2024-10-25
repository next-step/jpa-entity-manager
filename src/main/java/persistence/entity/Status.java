package persistence.entity;

import java.util.Set;

public enum Status {
    MANAGED {
        @Override
        Set<Status> getValidStatusTransitions() {
            return Set.of(MANAGED, DELETED, GONE);
        }
    },
    READ_ONLY {
        @Override
        Set<Status> getValidStatusTransitions() {
            return Set.of();
        }
    },
    DELETED {
        @Override
        Set<Status> getValidStatusTransitions() {
            return Set.of(MANAGED);
        }
    },
    GONE {
        @Override
        Set<Status> getValidStatusTransitions() {
            return Set.of();
        }
    },
    LOADING {
        @Override
        Set<Status> getValidStatusTransitions() {
            return Set.of(MANAGED);
        }
    },
    SAVING {
        @Override
        Set<Status> getValidStatusTransitions() {
            return Set.of(MANAGED);
        }
    };

    abstract Set<Status> getValidStatusTransitions();

    public boolean isValidStatusTransition(Status newStatus) {
        return getValidStatusTransitions().contains(newStatus);
    }

    public boolean isManaged() {
        return this == MANAGED;
    }
}
