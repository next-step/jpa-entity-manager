package persistence.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.usecase.CreateSnapShotObject;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.vo.DatabaseField;

public class PersistenceContextImpl<T> implements PersistenceContext<T> {
    private final Map<Long, T> cache = new ConcurrentHashMap<>();
    private final Map<Long, T> snapShot = new ConcurrentHashMap<>();
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;
    private final CreateSnapShotObject createSnapShotObject;

    public PersistenceContextImpl(GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase, GetFieldValueUseCase getFieldValueUseCase, CreateSnapShotObject createSnapShotObject) {
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
        this.createSnapShotObject = createSnapShotObject;
    }

    @Override
    public T getEntity(Long id) {
        return cache.get(id);
    }


    @Override
    public void addEntity(Long id, T entity) {
        if (id == null || entity == null) {
            return;
        }
        cache.put(id, entity);
        snapShot.put(id, (T) createSnapShotObject.execute(entity));
    }

    @Override
    public void removeEntity(T entity) {
        DatabaseField databaseField = getIdDatabaseFieldUseCase.execute(entity.getClass());
        Long id = (Long) getFieldValueUseCase.execute(entity, databaseField);
        cache.remove(id);
        snapShot.remove(id);
    }

    @Override
    public T getDatabaseSnapshot(Long id) {
        return snapShot.get(id);
    }

    @Override
    public void clear() {
        cache.clear();
        snapShot.clear();
    }
}
