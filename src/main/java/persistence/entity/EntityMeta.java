package persistence.entity;

import persistence.persistencecontext.EntityKey;
import persistence.sql.domain.IdColumn;
import persistence.sql.domain.Table;
import utils.ValueExtractor;
import utils.ValueInjector;

public class EntityMeta {
    private final Table table;
    private final IdColumn id;
    private final Object entity;
    private final EntityEntry entityEntry;

    public EntityMeta(Table table, IdColumn id, Object entity) {
        this.table = table;
        this.id = id;
        this.entity = entity;
        this.entityEntry = new EntityEntry();
    }

    public static <T> EntityMeta from(T entity) {
        Table table = Table.from(entity.getClass());
        IdColumn id = table.getIdColumn();
        return new EntityMeta(table, id, entity);
    }

    public EntityKey getEntityKey() {
        return new EntityKey(getId(), entity.getClass());
    }

    public boolean isNew() {
        return getId() == null;
    }

    public Object getId() {
        return ValueExtractor.extract(entity, id);
    }

    public void injectId(Object generatedId) {
        ValueInjector.inject(entity, id, generatedId);
    }

    public Object getEntity() {
        return entity;
    }

    public void updateStatus(EntityStatus status) {
        this.entityEntry.updateStatus(status);
    }
}
