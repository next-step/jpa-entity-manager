package persistence.entity;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaEntity;
import persistence.sql.dml.builder.SelectQueryBuilder;
/*
TODO
1. Persist 할때, 객체 값 변했는지 확인하고 update 해줘야 될것같습니다. 없으면 insert

2. value 값을 map 하는 객체가 존재하지 않아서, persist 로직 구현이 힘들어 보이는데 refactoring 해볼까요??
   이쪽은 Review 주시는대로 따라가겠습니다!!!
3. jdbcTemplate 의존도 제거하고싶은데 해당 부분은 Loader 하면서 진행할것같습니다!
 */

public class JdbcEntityManager implements EntityManager {

  private final JdbcTemplate jdbcTemplate;
  private final Connection connection;
  private final Map<Class<?>, EntityPersister> persisterMap = new ConcurrentHashMap<>();

  public JdbcEntityManager(JdbcTemplate jdbcTemplate, Connection connection) {

    this.jdbcTemplate = jdbcTemplate;
    this.connection = connection;
  }

  @Override
  public <T> T find(Class<T> clazz, Long id) {
    MetaEntity<T> metaEntity = MetaEntity.of(clazz);

    MetaDataColumn primaryKeyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = primaryKeyColumn.getDBColumnName();

    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    String query = selectQueryBuilder.createSelectByFieldQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, id);

    return jdbcTemplate.queryForObject(query, new JdbcRowMapper<>(metaEntity));
  }

  @Override
  public void persist(Object entity) {
    EntityPersister persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister(entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

    persister.insert(entity);
  }

  @Override
  public void remove(Object entity) {
    EntityPersister persister = persisterMap.getOrDefault(entity.getClass(),
        new JdbcEntityPersister(entity.getClass(), connection));
    persisterMap.putIfAbsent(entity.getClass(), persister);

    persister.delete(entity);
  }

}
