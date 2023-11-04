package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaEntity;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class JdbcEntityLoader<T> implements EntityLoader {

  private final JdbcTemplate jdbcTemplate;
  private final MetaEntity<T> metaEntity;
  private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
  private final JdbcRowMapper<T> jdbcRowMapper;

  public JdbcEntityLoader(Class<T> clazz, Connection connection) {
    this.jdbcTemplate = new JdbcTemplate(connection);
    this.metaEntity = MetaEntity.of(clazz);
    this.jdbcRowMapper = new JdbcRowMapper<>(metaEntity);
  }

  @Override
  public T load(Long id) {
    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = keyColumn.getDBColumnName();

    String query = selectQueryBuilder.createSelectByFieldQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, id);

    return jdbcTemplate.queryForObject(query, jdbcRowMapper);
  }
}
