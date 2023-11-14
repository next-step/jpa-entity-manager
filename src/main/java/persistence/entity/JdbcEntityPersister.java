package persistence.entity;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jdbc.JdbcTemplate;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;
import persistence.sql.dml.builder.DmlQueryBuilder;

public class JdbcEntityPersister<T> implements EntityPersister<T> {

  private final JdbcTemplate jdbcTemplate;
  private final MetaEntity<T> metaEntity;
  private final EntityLoader<T> entityLoader;
  private final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

  public JdbcEntityPersister(Class<T> clazz, Connection connection) {
    this.jdbcTemplate = new JdbcTemplate(connection);
    this.metaEntity = MetaEntity.of(clazz);
    this.entityLoader = new JdbcEntityLoader<T>(clazz, connection);
  }

  @Override
  public boolean update(Object entity) throws RuntimeException{

    MetaDataColumn primaryKeyColumn = metaEntity.getPrimaryKeyColumn();
    String whereColumn = primaryKeyColumn.getDBColumnName();
    Object id = primaryKeyColumn.getFieldValue(entity);

    MetaDataColumns metaDataColumns = metaEntity.getMetaDataColumns();
    List<String> fields = metaDataColumns.getFields();
    List<String> extractValuesFromEntity = metaEntity.extractValuesFromEntity(fields, entity);
    List<String> dbColumns = fields.stream()
        .map(field -> metaDataColumns.getColumnByFieldName(field).getDBColumnName())
        .collect(Collectors.toList());

    throwExceptionIfNotExists((Long) id, entity);

    String query = dmlQueryBuilder.createUpdateQuery(metaEntity.getTableName(),
        dbColumns, extractValuesFromEntity,
        whereColumn, id.toString());

    return jdbcTemplate.execute(query);
  }

  @Override
  public Long insert(Object entity) {
    String values = metaEntity.getValueClause(entity);
    String columns = metaEntity.getColumnClause();

    String query = dmlQueryBuilder.createInsertQuery(metaEntity.getTableName(), columns, values);

    Long id =  jdbcTemplate.executeWithGeneratedKey(query);
    metaEntity.getPrimaryKeyColumn().setFieldValue(entity, id);

    return id;
  }

  @Override
  public void delete(Object entity) throws RuntimeException {
    MetaDataColumn primaryKeyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = primaryKeyColumn.getDBColumnName();
    Object id = primaryKeyColumn.getFieldValue(entity);

    throwExceptionIfNotExists((Long) id, entity);

    String query = dmlQueryBuilder.createDeleteQuery(metaEntity.getTableName(), targetColumn, id);

    jdbcTemplate.execute(query);
  }

  public void throwExceptionIfNotExists(Long id, Object entity) {
    if (load(id).isEmpty() && metaEntity.getPrimaryKeyColumnIsNonNull(entity)) {
      throw new RuntimeException("해당 객체는 존재 하지 않습니다.");
    }
  }
  @Override
  public boolean entityExists(Object entity) {
    return metaEntity.getPrimaryKeyColumnIsNonNull(entity) &&
          entityLoader.load(metaEntity.getPrimaryKeyColumnValue(entity)).isPresent();
  }
  @Override
  public Optional<Long> getEntityId(Object entity) {
    return Optional.ofNullable(metaEntity.getPrimaryKeyColumnValue(entity));
  }

  public Optional<T> load(Long id) {
    return entityLoader.load(id);
  }

  public List<T> loadAll(List<Long> ids) {
    return entityLoader.loadByIds(ids);
  }
}
