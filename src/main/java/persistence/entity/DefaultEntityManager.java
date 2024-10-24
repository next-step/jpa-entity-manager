package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    private DefaultEntityManager(PersistenceContext persistenceContext, EntityPersister entityPersister,
                                 EntityLoader entityLoader) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(
                new DefaultPersistenceContext(),
                new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(), new UpdateQueryBuilder(), new DeleteQueryBuilder()),
                new DefaultEntityLoader(jdbcTemplate, new SelectQueryBuilder())
        );
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final T managedEntity = persistenceContext.getEntity(entityType, id);
        if (Objects.nonNull(managedEntity)) {
            return managedEntity;
        }

        final T entity = entityLoader.load(entityType, id);
        persistenceContext.addEntity(entity);
        return entity;
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addToPersistQueue(entity);

    }

    @Override
    public void remove(Object entity) {
        persistenceContext.addToRemoveQueue(entity);
    }

    @Override
    public void flush() {
        persist();
        delete();
        update();
    }

    private void persist() {
        final Queue<Object> persistQueue = persistenceContext.getPersistQueue();
        while (!persistQueue.isEmpty()) {
            final Object entity = persistQueue.poll();
            entityPersister.insert(entity);
            persistenceContext.addEntity(entity);
        }
    }

    private void delete() {
        final Queue<Object> removeQueue = persistenceContext.getRemoveQueue();
        while (!removeQueue.isEmpty()) {
            final Object entity = removeQueue.poll();
            entityPersister.delete(entity);
            persistenceContext.removeEntity(entity);
        }
    }

    private void update() {
        persistenceContext.getAllEntity()
                .forEach(this::update);
    }

    private void update(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        final Object snapshot = persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue());
        if (Objects.isNull(snapshot)) {
            return;
        }

        final List<EntityColumn> dirtiedEntityColumns = getDirtiedEntityColumns(entity, snapshot);
        if (dirtiedEntityColumns.isEmpty()) {
            return;
        }

        entityPersister.update(entity, dirtiedEntityColumns);
        persistenceContext.addEntity(entity);
    }

    private List<EntityColumn> getDirtiedEntityColumns(Object entity, Object snapshot) {
        final EntityTable entityTable = new EntityTable(entity);
        final EntityTable snapshotEntityTable = new EntityTable(snapshot);
        return IntStream.range(0, entityTable.getColumnCount())
                .filter(i -> isDirtied(entityTable.getEntityColumn(i), snapshotEntityTable.getEntityColumn(i)))
                .mapToObj(entityTable::getEntityColumn)
                .collect(Collectors.toList());
    }

    private boolean isDirtied(EntityColumn entityColumn, EntityColumn snapshotEntityColumn) {
        return !Objects.equals(entityColumn.getValue(), snapshotEntityColumn.getValue());
    }
}
