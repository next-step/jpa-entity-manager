package persistence.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.vo.DatabaseField;

public class PersistenceContextImpl<T> implements PersistenceContext<T> {
    private final Map<Long, T> cache = new ConcurrentHashMap<>();
    private final Map<Long, T> snapShot = new ConcurrentHashMap<>();
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;

    public PersistenceContextImpl(GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase, GetFieldValueUseCase getFieldValueUseCase) {
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
    }

    @Override
    public T getEntity(Long id) {
        System.out.println("########### get " + cache.get(id).toString());
        return cache.get(id);
    }


    @Override
    public void addEntity(Long id, T entity) {
        System.out.println("########### add " + entity.toString());
        cache.put(id, entity);
    }

    @Override
    public void removeEntity(T entity) {
        System.out.println("########### remove " + entity);
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
    public void addDatabaseSnapshot(Long id, T entity) {
        snapShot.put(id, entity);
    }
}
