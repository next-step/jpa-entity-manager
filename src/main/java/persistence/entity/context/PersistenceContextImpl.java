package persistence.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.vo.DatabaseField;

public class PersistenceContextImpl<T> implements PersistenceContext {
    private final Map<Long, T> context = new ConcurrentHashMap<>();
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;

    public PersistenceContextImpl(GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase, GetFieldValueUseCase getFieldValueUseCase) {
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
    }

    @Override
    public Object getEntity(Long id) {
        return context.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        context.put(id, (T) entity);
    }

    @Override
    public void removeEntity(Object entity) {
        DatabaseField databaseField = getIdDatabaseFieldUseCase.execute(entity.getClass());
        Long id = (Long) getFieldValueUseCase.execute(entity, databaseField);
        context.remove(id);
    }
}
