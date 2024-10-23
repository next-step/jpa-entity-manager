package persistence.sql;

import persistence.sql.context.EntityPersister;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.impl.DefaultEntityManager;

public record EntityManagerFactory(EntityPersister entityPersister) {
    public EntityManager createEntityManager() {
        return new DefaultEntityManager(new DefaultPersistenceContext(), entityPersister);
    }
}
