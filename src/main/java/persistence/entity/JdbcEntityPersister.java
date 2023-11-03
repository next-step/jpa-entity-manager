package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.dialect.h2.H2Dialect;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaDataTable;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;

/*
TODO
- 엔티티클래스와 테이블 간의 맵핑 및 데이터베이스 작업(CRUD 작업) 처리
- EntityPersister(class) 받아서 update, insert, delete 수행하면 된다.
- Persister에 Entity에 대한 column, table, template 다가져와서 만들어 두면 된다.
 (캐싱을 이미 해두고 사용하려고 하는것으로 보임) Persister를 불변객체로 사용하면 될듯
 - Dialect를 어디로 옮겨야 될까? EntityManager일까 아니면 맵핑을 직접하는 Columns 일까? -> 주입받아서 쓰면 최고일것같다.
 - update는 왜 갑자기 boolean이 필요할까? update 이후 상태 가 필요한가보네
 */
public class JdbcEntityPersister implements EntityPersister {

  private final JdbcTemplate jdbcTemplate;
  private final MetaDataTable metaDataTable;
  private final MetaDataColumns metaDataColumns;
  private final MetaDataColumn primaryKeyColumn;
  private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
  private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
  private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

  public JdbcEntityPersister(Class<?> clazz, Connection connection) {
    this.jdbcTemplate = new JdbcTemplate(connection);
    this.metaDataTable = MetaDataTable.of(clazz);
    this.metaDataColumns = MetaDataColumns.of(clazz, new H2Dialect());
    this.primaryKeyColumn = metaDataColumns.getPrimaryColumn();
  }

  @Override
  public boolean update(Object entity, String fieldName) {
    String whereColumn = primaryKeyColumn.getDBColumnName();
    String id = primaryKeyColumn.getFieldValue(entity).toString();

    MetaDataColumn targetColumn = metaDataColumns.getColumnByFieldName(fieldName);
    MetaDataColumn column = metaDataColumns.getColumnByFieldName(fieldName);
    String fieldValue = column.getFieldValue(entity).toString();

    String query = updateQueryBuilder.createUpdateQuery(metaDataTable.getName(), targetColumn.getDBColumnName(), fieldValue,
        whereColumn, id);

    return jdbcTemplate.execute(query);
  }

  @Override
  public void insert(Object entity) {
    String values = metaDataColumns.getClause(metaDataColumns.getFieldValuesWithoutId(entity));
    String columns = metaDataColumns.getClause(metaDataColumns.getDBColumnsWithoutId());

    String query = insertQueryBuilder.createInsertQuery(metaDataTable.getName(),columns, values);

    jdbcTemplate.execute(query);
  }

  @Override
  public void delete(Object entity) {
    String targetColumn = primaryKeyColumn.getDBColumnName();
    Object id = primaryKeyColumn.getFieldValue(entity);

    String query = deleteQueryBuilder.createDeleteQuery(metaDataTable.getName(), targetColumn, id);

    jdbcTemplate.execute(query);
  }
}
