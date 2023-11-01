package persistence.sql.dml.builder;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.GeneralAttribute;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateQueryBuilder {
    private final Object old;
    private final Object updated;
    private final EntityAttribute entityAttribute;

    private UpdateQueryBuilder(Object old, Object updated, EntityAttribute entityAttribute) {
        this.old = old;
        this.updated = updated;
        this.entityAttribute = entityAttribute;
    }

    public static UpdateQueryBuilder of(Object oldObject, Object newObjectToUpdate, EntityAttribute entityAttribute) {
        return new UpdateQueryBuilder(oldObject, newObjectToUpdate, entityAttribute);
    }

    public String prepareStatement() {
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(entityAttribute.getTableName())
                .append(" SET ");

        List<String> updates = new ArrayList<>();

        for (GeneralAttribute attribute : entityAttribute.getGeneralAttributes()) {
            String fieldName = attribute.getFieldName();

            Object oldValue = getAttributeValue(old, fieldName);
            Object newValue = getAttributeValue(updated, fieldName);

            if (!Objects.equals(oldValue, newValue)) {
                updates.add(attribute.getColumnName() + " = '" + newValue + "'");
            }
        }

        if (updates.isEmpty()) {
            return null;
        }

        sql.append(String.join(", ", updates))
                .append(" WHERE ")
                .append(entityAttribute.getIdAttribute().getColumnName())
                .append(" = '")
                .append(getAttributeValue(updated, entityAttribute.getIdAttribute().getColumnName()))
                .append("'");

        return sql.toString();
    }


    private Object getAttributeValue(Object obj, String attribute) {
        try {
            Field field = obj.getClass().getDeclaredField(attribute);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
