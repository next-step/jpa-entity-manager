package persistence.sql.context.impl;

import jdbc.RowMapper;
import persistence.sql.QueryBuilderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.clause.InsertColumnValueClause;
import persistence.sql.clause.UpdateQueryClauses;
import persistence.sql.clause.WhereConditionalClause;
import persistence.sql.common.util.NameConverter;
import persistence.sql.context.EntityPersister;
import persistence.sql.data.QueryType;
import persistence.sql.dml.Database;
import persistence.sql.dml.MetadataLoader;
import sample.application.RowMapperFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DefaultEntityPersister implements EntityPersister {
    private static final Logger logger = Logger.getLogger(DefaultEntityPersister.class.getName());

    private final Database database;
    private final NameConverter nameConverter;
    private final RowMapperFactory rowMapperFactory;


    public DefaultEntityPersister(Database database, NameConverter nameConverter, RowMapperFactory rowMapperFactory) {
        this.database = database;
        this.nameConverter = nameConverter;
        this.rowMapperFactory = rowMapperFactory;
    }

    @Override
    public <T> Object insert(T entity, MetadataLoader<?> loader) {
        InsertColumnValueClause clause = InsertColumnValueClause.newInstance(entity, nameConverter);

        String insertQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.INSERT, loader, clause);
        return database.executeUpdate(insertQuery);
    }

    @Override
    public <T> void update(T entity, MetadataLoader<?> loader) {
        UpdateQueryClauses updateQueryClauses = UpdateQueryClauses.builder(nameConverter)
                .where(entity, loader)
                .setColumnValues(entity, loader)
                .build();

        String mergeQuery = QueryBuilderFactory.getInstance()
                .buildQuery(QueryType.UPDATE, loader, updateQueryClauses.clauseArrays());
        database.executeUpdate(mergeQuery);
    }

    @Override
    public <T> void delete(T entity, MetadataLoader<?> loader) {
        Field pkField = loader.getPrimaryKeyField();
        Object extractedValue = Clause.extractValue(pkField, entity);
        String value = Clause.toColumnValue(extractedValue);

        WhereConditionalClause clause = WhereConditionalClause.builder()
                .column(loader.getColumnName(pkField, nameConverter))
                .eq(value);

        String removeQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.DELETE, loader, clause);
        database.executeUpdate(removeQuery);
    }

    @Override
    public <T> T select(Class<T> entityType, Object primaryKey, MetadataLoader<?> loader) {
        String value = Clause.toColumnValue(primaryKey);

        WhereConditionalClause clause = WhereConditionalClause.builder()
                .column(loader.getColumnName(loader.getPrimaryKeyField(), nameConverter))
                .eq(value);

        String selectQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.SELECT, loader, clause);

        RowMapper<T> rowMapper = rowMapperFactory.getRowMapper(entityType);
        if (rowMapper == null) {
            throw new IllegalStateException("RowMapper not found for entity type: " + entityType);
        }

        return database.executeQuery(selectQuery, resultSet -> {
            if (resultSet.next()) {
                return rowMapper.mapRow(resultSet);
            }

            return null;
        });
    }

    @Override
    public <T> List<T> selectAll(Class<T> entityType, MetadataLoader<?> loader) {
        String selectAllQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.SELECT, loader);

        RowMapper<T> rowMapper = rowMapperFactory.getRowMapper(entityType);

        if (rowMapper == null) {
            throw new IllegalStateException("RowMapper not found for entity type: " + entityType);
        }

        return database.executeQuery(selectAllQuery, resultSet -> {
            List<T> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(rowMapper.mapRow(resultSet));
            }

            return entities;
        });
    }
}
