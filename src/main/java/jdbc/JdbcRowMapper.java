package jdbc;

import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcRowMapper<T> implements RowMapper<T> {
  private final MetaEntity<T> metaEntity;

  public JdbcRowMapper(MetaEntity<T> metaEntity) {
    this.metaEntity = metaEntity;
  }
  @Override
  public T mapRow(ResultSet resultSet) throws SQLException {
    resultSet.next();

    T entityInstance = metaEntity.createInstance();
    MetaDataColumns metaDataColumns = metaEntity.getMetaDataColumns();

    for (MetaDataColumn metaDataColumn : metaDataColumns.getMetaDataColumns()) {
      String fieldName = metaDataColumn.getDBColumnName();
      metaDataColumn.setFieldValue(entityInstance, resultSet.getObject(fieldName));
    }
    return entityInstance;
  }

}
