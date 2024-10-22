package persistence.sql.loader;

import persistence.sql.QueryBuilderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.clause.WhereConditionalClause;
import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.common.util.NameConverter;
import persistence.sql.data.QueryType;
import persistence.sql.dml.Database;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EntityLoader<T> implements Loader<T>{
    private static final Logger logger = Logger.getLogger(EntityLoader.class.getName());

    private final Database database;
    private final MetadataLoader<T> metadataLoader;
    private final NameConverter nameConverter;

    public EntityLoader(Class<T> entityType, Database database) {
        this(database,
                new SimpleMetadataLoader<>(entityType),
                CamelToSnakeConverter.getInstance());
    }

    public EntityLoader(Database database,
                        MetadataLoader<T> metadataLoader,
                        NameConverter nameConverter) {
        this.database = database;
        this.metadataLoader = metadataLoader;
        this.nameConverter = nameConverter;
    }

    public MetadataLoader<T> getMetadataLoader() {
        return metadataLoader;
    }

    @Override
    public List<T> loadAll() {
        String selectQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.SELECT, metadataLoader);

        return database.executeQuery(selectQuery, resultSet -> {
            List<T> entities = new ArrayList<>();

            while (resultSet.next()) {
                entities.add(mapRow(resultSet));
            }

            return entities;
        });
    }

    @Override
    public T load(Object primaryKey) {
        String selectQuery = createSelectQuery(primaryKey);

        return database.executeQuery(selectQuery, resultSet -> {
            if (resultSet.next()) {
                return mapRow(resultSet);
            }

            return null;
        });
    }

    private String createSelectQuery(Object primaryKey) {
        String value = Clause.toColumnValue(primaryKey);

        WhereConditionalClause clause = WhereConditionalClause.builder()
                .column(metadataLoader.getColumnName(metadataLoader.getPrimaryKeyField(), nameConverter))
                .eq(value);

        return QueryBuilderFactory.getInstance().buildQuery(QueryType.SELECT, metadataLoader, clause);
    }

    public T mapRow(ResultSet resultSet) {
        try {
            T entity = metadataLoader.getNoArgConstructor().newInstance();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                Object columnValue = resultSet.getObject(i);

                Field field = metadataLoader.getField(i - 1);
                field.setAccessible(true);
                field.set(entity, columnValue);
            }

            return entity;
        } catch (ReflectiveOperationException | SQLException e) {
            logger.severe("Failed to map row to entity");
            throw new IllegalStateException(e);
        }
    }
}
