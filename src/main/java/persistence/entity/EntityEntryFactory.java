package persistence.entity;

interface EntityEntryFactory {
    EntityEntry createEntityEntry(EntityPersister entityPersister, EntityLoader entityLoader, Status status);
}
