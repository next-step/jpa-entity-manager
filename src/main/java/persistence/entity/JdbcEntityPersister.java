package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;
import persistence.sql.dml.builder.DmlQueryBuilder;

public class JdbcEntityPersister<T> implements EntityPersister {

  private final JdbcTemplate jdbcTemplate;
  private final MetaEntity<T> metaEntity;
  private final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();
  public JdbcEntityPersister(Class<T> clazz, Connection connection) {
    this.jdbcTemplate = new JdbcTemplate(connection);
    this.metaEntity = MetaEntity.of(clazz);
  }

  @Override
  public boolean update(Object entity, String fieldName) {

    MetaDataColumn primaryKeyColumn = metaEntity.getPrimaryKeyColumn();
    String whereColumn = primaryKeyColumn.getDBColumnName();
    String id = primaryKeyColumn.getFieldValue(entity).toString();

    MetaDataColumns metaDataColumns = metaEntity.getMetaDataColumns();
    MetaDataColumn targetColumn = metaDataColumns.getColumnByFieldName(fieldName);
    String fieldValue = targetColumn.getFieldValue(entity).toString();

    String query = dmlQueryBuilder.createUpdateQuery(metaEntity.getTableName(), targetColumn.getDBColumnName(), fieldValue,
        whereColumn, id);

    return jdbcTemplate.execute(query);
  }

  @Override
  public void insert(Object entity) {
    String values = metaEntity.getValueClause(entity);
    String columns = metaEntity.getColumnClause();

    String query = dmlQueryBuilder.createInsertQuery(metaEntity.getTableName(),columns, values);

    jdbcTemplate.execute(query);
  }

  @Override
  public void delete(Object entity) {
    MetaDataColumn primaryKeyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = primaryKeyColumn.getDBColumnName();
    Object id = primaryKeyColumn.getFieldValue(entity);

    String query = dmlQueryBuilder.createDeleteQuery(metaEntity.getTableName(), targetColumn, id);

    jdbcTemplate.execute(query);
  }
}
