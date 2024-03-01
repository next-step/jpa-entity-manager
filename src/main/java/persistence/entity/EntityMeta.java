package persistence.entity;

import persistence.persistencecontext.EntityKey;
import persistence.sql.domain.IdColumn;
import persistence.sql.domain.Table;
import utils.ValueExtractor;
import utils.ValueInjector;

public class EntityMeta {
    private final Table table;
    private final IdColumn id;

    public EntityMeta(Table table, IdColumn id) {
        this.table = table;
        this.id = id;
    }

    public static <T> EntityMeta from(T entity) {
        Table table = Table.from(entity.getClass());
        IdColumn id = table.getIdColumn();
        return new EntityMeta(table, id);
    }

    public EntityKey getEntityKey(Object entity) {
        return new EntityKey(getId(entity), table.getClazz());
    }

    public boolean isNew(Object entity) {
        return getId(entity) == null;
    }

    private Object getId(Object entity) {
        return ValueExtractor.extract(entity, id);
    }

    public void injectId(Object entity, Object generatedId) {
        ValueInjector.inject(entity, id, generatedId);
    }
}
