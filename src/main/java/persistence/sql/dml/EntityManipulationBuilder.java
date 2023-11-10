package persistence.sql.dml;

import persistence.sql.ddl.EntityMetadata;
import utils.CustomStringBuilder;

import static utils.CustomStringBuilder.*;

public class EntityManipulationBuilder {

    public String findAll(EntityMetadata entityMetadata) {
        return toFindAllStatement(entityMetadata.getTableName(), entityMetadata.getColumnNames());
    }

    public String findById(long id, EntityMetadata entityMetadata) {
        return toFindByIdStatement(
                entityMetadata.getTableName(),
                entityMetadata.getColumnNames(),
                entityMetadata.getIdColumnName(),
                String.valueOf(id)
        );
    }

    public String insert(Object entity, EntityMetadata entityMetadata) {
        return new CustomStringBuilder()
                .append(columnsClause(entity, entityMetadata))
                .append(valuesClause(entity, entityMetadata))
                .toString();
    }

    public String update(Object entity, Object snapshot, EntityMetadata entityMetadata) {
        String updateClause = entityMetadata.getUpdateClause(entity, snapshot);
        if (updateClause.isEmpty()) {
            return "";
        }

        return toUpdateStatement(
                entityMetadata.getTableName(),
                entityMetadata.getIdColumnName(),
                entityMetadata.getIdColumnValue(entity),
                updateClause
        );
    }

    public String delete(String id, EntityMetadata entityMetadata) {
        return toDeleteStatement(
                entityMetadata.getTableName(),
                entityMetadata.getIdColumnName(),
                id);
    }

    private String columnsClause(Object entity, EntityMetadata entityMetadata) {
        return toInsertColumnsClause(entityMetadata.getTableName(), entityMetadata.getColumnNames(entity));
    }

    private String valuesClause(Object entity, EntityMetadata entityMetadata) {
        return toInsertValuesClause(entityMetadata.getValueFrom(entity));
    }

}
