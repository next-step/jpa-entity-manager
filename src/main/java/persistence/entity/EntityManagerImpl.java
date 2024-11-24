package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate);
    }

    public void beginTransaction() {
        this.persistenceContext = new PersistenceContextImpl();
    }

    public void commitTransaction() {
        Map<Class<?>, List<EntityEntry>> entityEntries = this.persistenceContext.getEntityEntries();
        entityEntries.values().forEach(this::executeEntityActions);
    }

    private void executeEntityActions(List<EntityEntry> entryList) {
        for (EntityEntry entityEntry : entryList) {
            switch (entityEntry.getStatus()) {
                case MANAGED:
                    this.entityPersister.update(entityEntry.getEntity());
                    break;
                case DELETED:
                    this.entityPersister.delete(entityEntry.getEntityClass(), entityEntry.getId());
                    break;
                case READ_ONLY:
                case GONE:
                case LOADING:
                case SAVING:
                default:
                    break;
            }
        }
    }

    @Override
    public Object find(Class<?> clazz, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Unable to find without an ID!");
        }

        /* 관리 중인 엔티티라면 영속성 컨텍스트에서 꺼내서 반환 */
        Object managedEntity = persistenceContext.getEntity(clazz, id);
        if (managedEntity != null) {
            return managedEntity;
        }

        /* 관리 중이지 않다면 DB 조회 */
        Object entity = entityLoader.find(clazz, id);
        persistenceContext.putEntity(entity);
        return entity;
    }

    @Override
    public void persist(Object newEntity) {
        Long generatedKey = entityPersister.insert(newEntity);
        Object insertedEntity = entityLoader.find(newEntity.getClass(), generatedKey);
        persistenceContext.putEntity(insertedEntity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
