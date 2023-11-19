package persistence.common;

import java.util.List;

// entity, fieldValueList aggregation clazz
public class EntityMetadata {

    private final EntityClazz entityClazz;
    private final FieldValueList fieldValueList;

    public EntityMetadata(Object entity) {
        this.entityClazz = new EntityClazz(entity.getClass());
        this.fieldValueList = new FieldValueList(entity);
    }

    public String getTableName() {
        return entityClazz.getName();
    }

    public List<FieldValue> getIdFVList() {
        return fieldValueList.getIdFVList();
    }
}
