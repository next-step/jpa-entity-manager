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
    public <T> T find(Class<T> clazz, Object id) {
        EntityKey<T> entitykey = new EntityKey<>(id, clazz);
        Object persistObject = this.persistenceContext.findEntity(entitykey);
        if (persistObject != null) {
            return clazz.cast(persistObject);
        }
        T findObject = this.entityLoader.find(clazz, id);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(findObject);

        this.persistenceContext.insertEntity(new EntityKey<>(id, findObject.getClass()), dmlBuilderData);
        this.persistenceContext.insertDatabaseSnapshot(new EntityKey<>(id, findObject.getClass()), findObject);
        return findObject;
    }

    @Override
    public void persist(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.entityPersister.persist(dmlBuilderData);
        this.persistenceContext.insertEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
        this.persistenceContext.insertDatabaseSnapshot(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    @Override
    public void merge(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.entityLoader.find(dmlBuilderData.getClazz(), dmlBuilderData.getId());

        DMLBuilderData diffBuilderData = checkDirtyCheck(dmlBuilderData);

        if (diffBuilderData.getColumns().isEmpty()) {
            return;
        }

        this.entityPersister.merge(checkDirtyCheck(dmlBuilderData));
        this.persistenceContext.insertEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
        this.persistenceContext.insertDatabaseSnapshot(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    @Override
    public void remove(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.entityPersister.remove(dmlBuilderData);
        this.persistenceContext.deleteEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()));
    }

    private DMLBuilderData checkDirtyCheck(DMLBuilderData entityBuilderData) {
        EntityKey<?> entityKey = new EntityKey<>(entityBuilderData.getId(), entityBuilderData.getClazz());

        Object snapshotObject = this.persistenceContext.getDatabaseSnapshot(entityKey);

        List<DMLColumnData> differentColumns = getDifferentColumns(entityBuilderData, DMLBuilderData.createDMLBuilderData(snapshotObject));

        return entityBuilderData.changeColumns(differentColumns);
    }

    private List<DMLColumnData> getDifferentColumns(DMLBuilderData entityBuilderData, DMLBuilderData snapShotBuilderData) {
        Map<String, DMLColumnData> snapShotColumnMap = convertDMLColumnDataMap(snapShotBuilderData);

        return entityBuilderData.getColumns().stream()
                .filter(entityColumn -> {
                    DMLColumnData persistenceColumn = snapShotColumnMap.get(entityColumn.getColumnName());
                    return !entityColumn.getColumnValue().equals(persistenceColumn.getColumnValue());
                })
                .toList();
    }

    private Map<String, DMLColumnData> convertDMLColumnDataMap(DMLBuilderData dmlBuilderData) {
        return dmlBuilderData.getColumns().stream()
                .collect(Collectors.toMap(DMLColumnData::getColumnName, Function.identity()));
    }
}
