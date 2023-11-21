package persistence.sql.dml;

import jakarta.persistence.GenerationType;
import persistence.sql.dml.delete.DeleteQuery;
import persistence.sql.dml.insert.InsertQuery;
import persistence.sql.dml.select.SelectQuery;
import persistence.sql.dml.update.UpdateQuery;
import persistence.sql.dml.where.ConditionType;
import persistence.sql.dml.where.WhereQuery;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseField;
import persistence.sql.usecase.GetTableNameFromClass;
import persistence.sql.vo.DatabaseField;
import persistence.sql.vo.DatabaseFields;
import persistence.sql.vo.TableName;

public class DataManipulationLanguageGenerator {
    private final GetTableNameFromClass getTableNameFromClass;
    private final GetFieldFromClass getFieldFromClass;
    private final GetFieldValue getFieldValue;
    private final GetIdDatabaseField getIdDatabaseField;

    public DataManipulationLanguageGenerator(GetTableNameFromClass getTableNameFromClass, GetFieldFromClass getFieldFromClass, GetFieldValue getFieldValue,
                                             GetIdDatabaseField getIdDatabaseField) {
        this.getTableNameFromClass = getTableNameFromClass;
        this.getFieldFromClass = getFieldFromClass;
        this.getFieldValue = getFieldValue;
        this.getIdDatabaseField = getIdDatabaseField;
    }

    public InsertQuery buildInsertQuery(Object object) {
        TableName tableName = getTableNameFromClass.execute(object.getClass());
        DatabaseFields databaseFields = getFieldFromClass.execute(object.getClass());
        InsertQuery insertQuery = new InsertQuery(tableName);
        for (DatabaseField databaseField : databaseFields.getDatabaseFields()) {
            if (databaseField.isPrimary() && databaseField.getPrimaryKeyGenerationType() == GenerationType.IDENTITY) {
                continue;
            }
            insertQuery.addFieldValue(new ColumnClause(databaseField.getDatabaseFieldName()), new ValueClause(getFieldValue.execute(object, databaseField), databaseField.getDatabaseType()));
        }
        return insertQuery;
    }

    public SelectQuery buildSelectQuery(Class<?> cls) {
        TableName tableName = getTableNameFromClass.execute(cls);
        return new SelectQuery(tableName);
    }

    public WhereQuery buildSelectWhereQuery(Class<?> cls, long id) {
        WhereQuery whereQuery = new WhereQuery();
        DatabaseField field = getIdDatabaseField.execute(cls);
        whereQuery.addKey(field.getDatabaseFieldName(), new ValueClause(id, field.getDatabaseType()), ConditionType.IS);
        return whereQuery;
    }

    public WhereQuery buildWhereQuery(Object object) {
        WhereQuery whereQuery = new WhereQuery();
        DatabaseField field = getIdDatabaseField.execute(object.getClass());
        Object value = getFieldValue.execute(object, field);
        if (value == null) {
            throw new NullPointerException("Id should not be null in whereClause");
        }
        whereQuery.addKey(field.getDatabaseFieldName(), new ValueClause(value, field.getDatabaseType()), ConditionType.IS);
        return whereQuery;
    }

    public DeleteQuery buildDeleteQuery(Class<?> cls) {
        TableName tableName = getTableNameFromClass.execute(cls);
        return new DeleteQuery(tableName);
    }

    public UpdateQuery buildUpdateQuery(Object object) {
        TableName tableName = getTableNameFromClass.execute(object.getClass());
        DatabaseFields databaseFields = getFieldFromClass.execute(object.getClass());
        UpdateQuery updateQuery = new UpdateQuery(tableName, buildWhereQuery(object));
        for (DatabaseField databaseField : databaseFields.getDatabaseFields()) {
            if (!databaseField.isPrimary()) {
                updateQuery.addFieldValue(new ColumnClause(databaseField.getDatabaseFieldName()),
                    new ValueClause(getFieldValue.execute(object, databaseField), databaseField.getDatabaseType()));
            }
        }
        return updateQuery;
    }
}
