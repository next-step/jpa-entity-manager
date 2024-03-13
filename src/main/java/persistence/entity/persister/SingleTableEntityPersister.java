package persistence.entity.persister;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.model.EntityIdentifierMapping;
import persistence.model.EntityMetaDataMapping;
import persistence.model.MetaDataModelMappingException;
import persistence.sql.dml.Delete;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.dml.Insert;
import persistence.sql.dml.Update;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

import java.lang.reflect.Field;

public class SingleTableEntityPersister implements EntityPersister {

    private final String name;
    private final TableBinder tableBinder;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final EntityIdentifierMapping identifierMapping;

    public SingleTableEntityPersister(final String name, final TableBinder tableBinder, final DmlQueryBuilder dmlQueryBuilder, final JdbcTemplate jdbcTemplate, final Class<?> clazz) {
        this.name = name;
        this.tableBinder = tableBinder;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        final Field idField = getIdField();
        this.identifierMapping = new EntityIdentifierMapping(clazz, idField.getName(), idField);
    }

    private Field getIdField() {
        return EntityMetaDataMapping.getMetaData(this.name)
                .getFields()
                .stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new MetaDataModelMappingException("id field not found"));
    }

    public String getTargetEntityName() {
        return this.name;
    }

    @Override
    public boolean update(final Object entity) {
        final Table table = tableBinder.createTable(entity);
        final String updateQuery = dmlQueryBuilder.buildUpdateQuery(new Update(table));

        try {
            jdbcTemplate.execute(updateQuery);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public Object insert(final Object entity) {
        final Table table = tableBinder.createTable(entity);
        final boolean hasIdentifierKey = table.getPrimaryKey().hasIdentifierKey();

        final String insertQuery = dmlQueryBuilder.buildInsertQuery(new Insert(table));

        Object key = null;

        if (hasIdentifierKey) {
            key = jdbcTemplate.executeWithGeneratedKey(insertQuery);
        } else {
            jdbcTemplate.execute(insertQuery);
        }

        return key;
    }

    @Override
    public void delete(final Object entity) {
        final Table table = tableBinder.createTable(entity);

        final String deleteQuery = dmlQueryBuilder.buildDeleteQuery(new Delete(table));

        jdbcTemplate.execute(deleteQuery);
    }

    @Override
    public Object getIdentifier(final Object entity) {
        return identifierMapping.getIdentifier(entity);
    }

    @Override
    public void setIdentifier(final Object entity, final Object value) {
        identifierMapping.setIdentifierValue(entity, value);
    }
}
