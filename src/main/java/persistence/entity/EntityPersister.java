package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.ddl.exception.IdAnnotationMissingException;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.dml.WhereBuilder;
import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import java.lang.reflect.Field;
import java.util.Arrays;

import static persistence.sql.dml.BooleanExpression.eq;

public interface EntityPersister {
    boolean update(Object entity);
    void insert(Object entity);
    void delete(Object entity);
}
