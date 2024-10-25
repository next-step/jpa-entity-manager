package persistence;

import builder.dml.DMLBuilderData;
import builder.dml.DMLColumnData;
import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.persistenceContext = new PersistenceContextImpl();
    }

    public EntityManagerImpl(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityKey<T> entityObject = new EntityKey<>(id, clazz);
        Object persistObject = this.persistenceContext.findEntity(entityObject);
        if (persistObject != null) {
            return clazz.cast(persistObject);
        }
        T findObject = this.entityLoader.find(clazz, id);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(findObject);

        this.persistenceContext.insertEntity(new EntityKey<>(id, findObject.getClass()), dmlBuilderData);
        this.persistenceContext.addDatabaseSnapshot(new EntityKey<>(id, findObject.getClass()), findObject);
        return findObject;
    }

    @Override
    public void persist(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.entityPersister.persist(dmlBuilderData);
        this.persistenceContext.insertEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
        this.persistenceContext.addDatabaseSnapshot(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    @Override
    public void merge(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.entityPersister.merge(dmlBuilderData);
        this.persistenceContext.insertEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    @Override
    public void remove(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.entityPersister.remove(dmlBuilderData);
        this.persistenceContext.deleteEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()));
    }

    @Override
    public DMLBuilderData checkDirtyCheck(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        EntityKey<?> entityKey = new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass());

        Object persistenceObject = this.persistenceContext.findEntity(entityKey);
        Object snapshotObject = this.persistenceContext.getDatabaseSnapshot(entityKey);

        List<DMLColumnData> differentColumns = getDifferentColumns(DMLBuilderData.createDMLBuilderData(persistenceObject), DMLBuilderData.createDMLBuilderData(snapshotObject));

        confirmDifferentColumnsIsEmpty(differentColumns);

        return dmlBuilderData.changeColumns(differentColumns);
    }

    private List<DMLColumnData> getDifferentColumns(DMLBuilderData persistenceBuilderData, DMLBuilderData snapShotBuilderData) {
        Map<String, DMLColumnData> persistenceColumnMap = persistenceBuilderData.getColumns().stream()
                .collect(Collectors.toMap(DMLColumnData::getColumnName, Function.identity()));

        return snapShotBuilderData.getColumns().stream()
                .filter(snapshotColumn -> {
                    DMLColumnData persistenceColumn = persistenceColumnMap.get(snapshotColumn.getColumnName());
                    return !snapshotColumn.getColumnValue().equals(persistenceColumn.getColumnValue());
                })
                .toList();
    }

    private void confirmDifferentColumnsIsEmpty(List<DMLColumnData> differentColumns) {
        if (differentColumns.isEmpty()) {
            throw new IllegalStateException("SnapShot과 다른점이 없습니다.");
        }
    }
}
