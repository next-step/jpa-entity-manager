package persistence.entity.event;

import persistence.entity.EntityPersister;

public interface Event <T, ID> {

    void excetute(EntityPersister entityPersister);
}
