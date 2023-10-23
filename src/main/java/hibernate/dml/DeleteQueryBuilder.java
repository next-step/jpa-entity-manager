package hibernate.dml;

import hibernate.entity.EntityClass;

public class DeleteQueryBuilder {

    public static final DeleteQueryBuilder INSTANCE = new DeleteQueryBuilder();

    private static final String DELETE_QUERY = "delete from %s where %s = %s;";

    private DeleteQueryBuilder() {
    }

    public String generateQuery(final EntityClass<?> entityClass, final Object entity) {
        return String.format(DELETE_QUERY, entityClass.tableName(), entityClass.getEntityId().getFieldName(), entityClass.extractEntityId(entity));
    }
}
