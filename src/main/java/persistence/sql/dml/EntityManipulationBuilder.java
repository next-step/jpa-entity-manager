package persistence.sql.dml;

import persistence.sql.ddl.EntityMetadata;
import utils.CustomStringBuilder;

import static utils.CustomStringBuilder.*;

public class EntityManipulationBuilder {

    public static String insert(Object entity, EntityMetadata entityMetadata) {
        return new CustomStringBuilder()
                .append(columnsClause(entity, entityMetadata))
                .append(valuesClause(entity, entityMetadata))
                .toString();
    }

    private static String columnsClause(Object entity, EntityMetadata entityMetadata) {
        return toInsertColumnsClause(entityMetadata.getTableName(), entityMetadata.getColumnNames(entity));
    }

    private static String valuesClause(Object entity, EntityMetadata entityMetadata) {
        return toInsertValuesClause(entityMetadata.getValueFrom(entity));
    }

    public static String findAll(EntityMetadata entityMetadata) {
        return toFindAllStatement(entityMetadata.getTableName(), entityMetadata.getColumnNames());
    }

    public static String findById(long id, EntityMetadata entityMetadata) {
        return toFindByIdStatement(
                entityMetadata.getTableName(),
                entityMetadata.getColumnNames(),
                entityMetadata.getIdColumnName(),
                String.valueOf(id)
        );
    }

    public static String delete(String id, EntityMetadata entityMetadata) {
        return toDeleteStatement(
                entityMetadata.getTableName(),
                entityMetadata.getIdColumnName(),
                id);
    }

}
