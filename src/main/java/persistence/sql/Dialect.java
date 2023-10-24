package persistence.sql;

import persistence.sql.entity.EntityColumn;

/**
 * 각 DB 종류별 Dialect 정의
 */
public interface Dialect {

    String INSERT_STATEMENT = "insert into %s (%s) values (%s)";
    String CREATE_STATEMENT = "create table %s (%s)";
    String DROP_STATEMENT = "drop table if exists %s CASCADE"; // 필요할 경우 변경 예정

    String getDbType(EntityColumn entityColumn);

}
