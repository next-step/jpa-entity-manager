package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import persistence.dialect.h2.H2Dialect;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaDataTable;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class JdbcEntityLoader<T> implements EntityLoader {

  private final JdbcTemplate jdbcTemplate;
  private final MetaDataTable metaDataTable;
  private final MetaDataColumns metaDataColumns;
  private final MetaDataColumn primaryKeyColumn;
  private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
  private final JdbcRowMapper<T> jdbcRowMapper;

  public JdbcEntityLoader(Class<T> clazz, Connection connection) {
    this.jdbcTemplate = new JdbcTemplate(connection);
    this.metaDataTable = MetaDataTable.of(clazz);
    this.metaDataColumns = MetaDataColumns.of(clazz, new H2Dialect());
    this.primaryKeyColumn = metaDataColumns.getPrimaryColumn();
    this.jdbcRowMapper = new JdbcRowMapper<>(clazz);
  }

  @Override
  public T load(Long id) {
    String targetColumn = primaryKeyColumn.getDBColumnName();

    String query = selectQueryBuilder.createSelectByFieldQuery(metaDataColumns.getClause(metaDataColumns.getColumnsWithId()),
        metaDataTable.getName(), targetColumn, id);

    return jdbcTemplate.queryForObject(query, jdbcRowMapper);
  }
}
