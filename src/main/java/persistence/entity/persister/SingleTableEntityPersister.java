package persistence.entity.persister;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.model.EntityIdentifierMapping;
import persistence.model.MetaDataModelMappingException;
import persistence.sql.dml.Delete;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.dml.Insert;
import persistence.sql.dml.Update;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

import java.lang.reflect.Field;
import java.util.Arrays;

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
        // TODO 추후 4주차 미션에서 컴포넌트 스캔 단계 작업 시 컴포넌트 스캔으로 해결할 예정
        final Field idField = getIdField(clazz);
        this.identifierMapping = new EntityIdentifierMapping(clazz, idField.getName(), idField);
    }

    // TODO 추후 4주차 미션에서 컴포넌트 스캔 단계 작업 시 컴포넌트 스캔으로 해결할 예정
    private Field getIdField(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new MetaDataModelMappingException("id field not found"));
    }

    // TODO 추후 4주차 미션에서 컴포넌트 스캔 단계 작업 시 컴포넌트 스캔으로 해결할 예정
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
}
