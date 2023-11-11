package persistence.entity;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaEntity;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class JdbcEntityLoader<T> implements EntityLoader<T> {

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
  public Optional<T> load(Long id) {
    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = keyColumn.getDBColumnName();

    String query = selectQueryBuilder.createSelectByFieldQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, id);

    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject(query, jdbcRowMapper));
    } catch (RuntimeException e) {
      return Optional.empty();
    }

  }

  @Override
  public List<T> findAll() {
    String query = selectQueryBuilder.createSelectQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName());
    return jdbcTemplate.query(query, jdbcRowMapper);
  }

  @Override
  public List<T> loadByIds(List<Long> ids) {
    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = keyColumn.getDBColumnName();

    List<String> idValues = ids.stream().map(id -> id.toString()).collect(Collectors.toList());

    String query = selectQueryBuilder.createSelectByFieldsQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, idValues);

    return jdbcTemplate.query(query, jdbcRowMapper);
  }
}
